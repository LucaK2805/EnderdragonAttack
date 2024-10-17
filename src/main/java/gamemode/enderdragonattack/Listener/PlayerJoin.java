package gamemode.enderdragonattack.Listener;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Scoreboard.LobbyScoreboard;
import gamemode.enderdragonattack.LevelSystem.LevelSystem;
import gamemode.enderdragonattack.Utilitis.LobbyAreaMarker;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerJoin implements Listener {

    private static final String LOBBY_WORLD_NAME = "Lobby";
    private static final String LOBBY_LOAD_ERROR = ChatColor.RED + "The Lobby world is not available. Please contact an admin.";
    private static final Location LOBBY_SPAWN = new Location(null, -0.5, 118, -0.5);

    private final LobbyAreaMarker lobbyAreaMarker;
    private final JavaPlugin plugin;
    private final LobbyScoreboard lobbyScoreboard;
    private final String prefix;

    public PlayerJoin(JavaPlugin plugin, LevelSystem levelSystem, LobbyAreaMarker lobbyAreaMarker) {
        this.plugin = plugin;
        this.lobbyScoreboard = new LobbyScoreboard(plugin, levelSystem);
        this.lobbyAreaMarker = lobbyAreaMarker;

        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (loadLobbyWorld()) {
            teleportToLobby(player);
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.sendMessage(LOBBY_LOAD_ERROR);
        }

        lobbyScoreboard.createLobbyScoreboard(player);
        event.setJoinMessage(formatJoinMessage(player));
        lobbyAreaMarker.initialize();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(formatQuitMessage(event.getPlayer()));
    }

    private void teleportToLobby(Player player) {
        World lobbyWorld = Bukkit.getWorld(LOBBY_WORLD_NAME);
        if (lobbyWorld != null) {
            LOBBY_SPAWN.setWorld(lobbyWorld);
            player.teleport(LOBBY_SPAWN);
        }
    }

    private boolean loadLobbyWorld() {
        World lobbyWorld = Bukkit.getWorld(LOBBY_WORLD_NAME);
        if (lobbyWorld == null) {
            File lobbyWorldFolder = new File(Bukkit.getWorldContainer(), LOBBY_WORLD_NAME);
            if (lobbyWorldFolder.exists()) {
                lobbyWorld = Bukkit.createWorld(new WorldCreator(LOBBY_WORLD_NAME));
                return lobbyWorld != null;
            }
            return false;
        }
        return true;
    }

    private String formatJoinMessage(Player player) {
        return prefix + ChatColor.GRAY + "The player " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " joined the Server!";
    }

    private String formatQuitMessage(Player player) {
        return prefix + ChatColor.GRAY + "The player " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " left the Server!";
    }
}