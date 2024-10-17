package gamemode.enderdragonattack.Utilitis;

import gamemode.enderdragonattack.Start_Stop.Start;
import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Start_Stop.StartTimer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyAreaMarker implements CommandExecutor {

    private static final String LOBBY_WORLD_NAME = "lobby";
    private static final long INITIALIZATION_DELAY = 10L;
    private static final long PARTICLE_INTERVAL = 20L;
    private static final long PLAYER_TRACKING_INTERVAL = 10L;
    private static final double PARTICLE_SPACING = 0.5;
    private static final double PARTICLE_Y_OFFSET = -0.8;

    private final Plugin plugin;
    private final StartTimer startTimer;
    private final String prefix;
    private World lobbyWorld;
    private Location corner1;
    private Location corner2;
    private final List<UUID> playersInArea = new ArrayList<>();

    public LobbyAreaMarker(Plugin plugin, Start startCommand) {
        this.plugin = plugin;
        this.startTimer = startCommand.getStartTimer();
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    public void initialize() {
        new BukkitRunnable() {
            @Override
            public void run() {
                initializeWorld();
                if (lobbyWorld != null) {
                    initializeCorners();
                    startParticleTask();
                    startPlayerTrackingTask();
                }
            }
        }.runTaskLater(plugin, INITIALIZATION_DELAY);
    }

    private void initializeWorld() {
        lobbyWorld = Bukkit.getWorld(LOBBY_WORLD_NAME);
        if (lobbyWorld == null) {
            plugin.getLogger().severe("World '" + LOBBY_WORLD_NAME + "' is not loaded or does not exist!");
        }
    }

    private void initializeCorners() {
        corner1 = new Location(lobbyWorld, -3, 117, -5);
        corner2 = new Location(lobbyWorld, 2, 117, -11);
    }

    private void startParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (lobbyWorld == null) return;
                spawnBorderParticles();
            }
        }.runTaskTimer(plugin, 0L, PARTICLE_INTERVAL);
    }

    private void spawnBorderParticles() {
        for (double x = corner1.getX(); x <= corner2.getX(); x += PARTICLE_SPACING) {
            spawnParticle(x, corner1.getZ());
            spawnParticle(x, corner2.getZ());
        }
        for (double z = corner1.getZ(); z >= corner2.getZ(); z -= PARTICLE_SPACING) {
            spawnParticle(corner1.getX(), z);
            spawnParticle(corner2.getX(), z);
        }
    }

    private void spawnParticle(double x, double z) {
        lobbyWorld.spawnParticle(Particle.VILLAGER_HAPPY, new Location(lobbyWorld, x, corner1.getY() + PARTICLE_Y_OFFSET, z), 1);
    }

    private void startPlayerTrackingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (lobbyWorld == null) return;
                trackPlayers();
            }
        }.runTaskTimer(plugin, 0L, PLAYER_TRACKING_INTERVAL);
    }

    private void trackPlayers() {
        List<UUID> toRemove = new ArrayList<>();
        List<UUID> toAdd = new ArrayList<>();

        for (UUID uuid : playersInArea) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline() || !isInArea(player.getLocation())) {
                toRemove.add(uuid);
            }
        }

        for (Player player : lobbyWorld.getPlayers()) {
            UUID uuid = player.getUniqueId();
            if (isInArea(player.getLocation()) && !playersInArea.contains(uuid)) {
                toAdd.add(uuid);
            }
        }

        handlePlayerRemovals(toRemove);
        handlePlayerAdditions(toAdd);
    }

    private void handlePlayerRemovals(List<UUID> toRemove) {
        for (UUID uuid : toRemove) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(prefix + ChatColor.RED + "You left the starting zone");
                playersInArea.remove(uuid);
                startTimer.playerLeftLobby(uuid);
            }
        }
    }

    private void handlePlayerAdditions(List<UUID> toAdd) {
        for (UUID uuid : toAdd) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                if (Start.isGameRunning()) {
                    player.sendMessage(prefix + ChatColor.RED + "The game is currently running. Please wait.");
                } else {
                    player.sendMessage(prefix + ChatColor.GREEN + "You entered the starting zone");
                    playersInArea.add(uuid);
                    startTimer.playerEnteredLobby(uuid);
                }
            }
        }
    }

    private boolean isInArea(Location location) {
        return location.getX() >= corner1.getX() && location.getX() <= corner2.getX() &&
                location.getZ() <= corner1.getZ() && location.getZ() >= corner2.getZ();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("list")) {
            listPlayersInStartingZone(sender);
            return true;
        }
        return false;
    }

    private void listPlayersInStartingZone(CommandSender sender) {
        sender.sendMessage(prefix + ChatColor.GRAY + "Players in the starting zone:");
        for (UUID uuid : playersInArea) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                sender.sendMessage(prefix + ChatColor.GRAY + "- " + player.getName());
            }
        }
    }
}