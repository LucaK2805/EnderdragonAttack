package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class WorldRegenerateCommand implements CommandExecutor {

    private static final String COMMAND_NAME = "resetworld";
    private static final String GAME_WORLD_NAME = "GameWorld";
    private static final String SOURCE_WORLD_NAME = "ExampleWorld";
    private static final String LOBBY_WORLD_NAME = "Lobby";

    private final JavaPlugin plugin;
    private final String prefix;

    public WorldRegenerateCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!COMMAND_NAME.equalsIgnoreCase(command.getName())) {
            return false;
        }

        boolean resetSuccessful = resetWorld(GAME_WORLD_NAME, SOURCE_WORLD_NAME, LOBBY_WORLD_NAME);
        sender.sendMessage(prefix + (resetSuccessful ? ChatColor.GREEN + "The GameWorld was successfully reset!"
                : ChatColor.RED + "The GameWorld cannot be reset!"));
        return true;
    }

    public boolean resetWorld(String worldNameToDelete, String sourceWorldName, String lobbyWorldName) {
        World lobbyWorld = Bukkit.getWorld(lobbyWorldName);
        if (lobbyWorld == null) {
            plugin.getLogger().severe("The lobby world '" + lobbyWorldName + "' does not exist!");
            return false;
        }

        World worldToDelete = Bukkit.getWorld(worldNameToDelete);
        if (worldToDelete == null) {
            return false;
        }

        teleportPlayersToLobby(worldToDelete, lobbyWorld);
        if (!unloadWorld(worldToDelete)) {
            return false;
        }

        File worldDir = plugin.getServer().getWorldContainer().toPath().resolve(worldNameToDelete).toFile();
        if (!deleteDirectory(worldDir)) {
            return false;
        }

        if (!copyWorldFiles(sourceWorldName, worldNameToDelete)) {
            return false;
        }

        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.createWorld(new WorldCreator(worldNameToDelete)));
        return true;
    }

    private void teleportPlayersToLobby(World fromWorld, World toWorld) {
        fromWorld.getPlayers().forEach(player -> player.teleport(toWorld.getSpawnLocation()));
    }

    private boolean unloadWorld(World world) {
        for (int i = 0; i < 10; i++) {
            if (Bukkit.unloadWorld(world, false)) {
                return true;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                plugin.getLogger().log(Level.WARNING, "Interrupted while waiting to unload world", e);
            }
        }
        return false;
    }

    private boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    private boolean copyWorldFiles(String sourceWorldName, String targetWorldName) {
        Path source = Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(), sourceWorldName);
        Path target = Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(), targetWorldName);

        try {
            copyDirectory(source, target);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to copy world files", e);
            return false;
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(s -> {
                try {
                    Path d = target.resolve(source.relativize(s));
                    if (Files.isDirectory(s)) {
                        Files.createDirectories(d);
                    } else if (!"session.lock".equals(s.getFileName().toString())) {
                        Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error copying file: " + s + " to: " + target.resolve(source.relativize(s)), e);
                }
            });
        }
    }
}