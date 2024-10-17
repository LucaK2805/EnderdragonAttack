package gamemode.enderdragonattack.Scoreboard;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.LevelSystem.LevelSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyScoreboard implements Listener {

    private static final String LOBBY_WORLD_NAME = "lobby";
    private static final String OBJECTIVE_NAME = "lobby";
    private static final long UPDATE_INTERVAL = 20L; // 1 second in ticks

    private final Plugin plugin;
    private final PlayerDataBase playerDataBase;
    private final LevelSystem levelSystem;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private final String gradientPrefix;

    public LobbyScoreboard(Plugin plugin, LevelSystem levelSystem) {
        this.plugin = plugin;
        this.playerDataBase = new PlayerDataBase(plugin);
        this.levelSystem = levelSystem;
        this.playerScoreboards = new HashMap<>();
        this.gradientPrefix = new Gradient().generateGradient("★ Enderdragonattack ★");

        Bukkit.getPluginManager().registerEvents(this, plugin);
        startUpdatingScoreboards();
    }

    public void createLobbyScoreboard(Player player) {
        if (!isInLobby(player)) {
            return;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(OBJECTIVE_NAME, "dummy", gradientPrefix);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        setupScoreboardLines(board, objective, player);

        player.setScoreboard(board);
        playerScoreboards.put(player.getUniqueId(), board);
    }

    private void setupScoreboardLines(Scoreboard board, Objective objective, Player player) {
        createOrUpdateTeam(board, "playerName", ChatColor.WHITE + " ", player.getName());
        createOrUpdateTeam(board, "playerLevel", ChatColor.WHITE + "  ", String.valueOf(levelSystem.getPlayerLevel(player)));
        createOrUpdateTeam(board, "playerCoins", ChatColor.WHITE + "   ", String.valueOf(playerDataBase.getPlayerCoins(player)));
        createOrUpdateTeam(board, "playerPlaytime", ChatColor.WHITE + "    ", formatPlaytime(playerDataBase.getPlayerPlaytime(player)));

        String[] lines = {
                "     ", ChatColor.YELLOW + "✔ Name: ", ChatColor.WHITE + " ", " ",
                ChatColor.GREEN + "♦ Level: ", ChatColor.WHITE + "  ", "  ",
                ChatColor.GOLD + "♦ Coins: ", ChatColor.WHITE + "   ", "   ",
                ChatColor.BLUE + "♥ Playtime: ", ChatColor.WHITE + "    "
        };

        for (int i = 0; i < lines.length; i++) {
            objective.getScore(lines[i]).setScore(11 - i);
        }
    }

    private void createOrUpdateTeam(Scoreboard board, String name, String entry, String suffix) {
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
        }
        team.addEntry(entry);
        team.setSuffix(suffix);
    }

    private void startUpdatingScoreboards() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isInLobby(player)) {
                        updateScoreboard(player);
                    } else {
                        removeLobbyScoreboard(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, UPDATE_INTERVAL);
    }

    private void updateScoreboard(Player player) {
        Scoreboard board = playerScoreboards.get(player.getUniqueId());
        if (board == null) {
            createLobbyScoreboard(player);
            return;
        }

        updateTeamSuffix(board, "playerLevel", String.valueOf(levelSystem.getPlayerLevel(player)));
        updateTeamSuffix(board, "playerCoins", String.valueOf(playerDataBase.getPlayerCoins(player)));
        updateTeamSuffix(board, "playerPlaytime", formatPlaytime(playerDataBase.getPlayerPlaytime(player)));
    }

    private void updateTeamSuffix(Scoreboard board, String name, String suffix) {
        Team team = board.getTeam(name);
        if (team != null) {
            team.setSuffix(suffix);
        }
    }

    private String formatPlaytime(long playtime) {
        long days = playtime / 86400;
        long remainingSecondsAfterDays = playtime % 86400;
        long hours = remainingSecondsAfterDays / 3600;
        long minutes = (remainingSecondsAfterDays % 3600) / 60;

        return String.format("%dd %dh %dm", days, hours, minutes);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataBase.addNewPlayer(player);
        createLobbyScoreboard(player);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (isInLobby(player)) {
            createLobbyScoreboard(player);
        } else {
            removeLobbyScoreboard(player);
        }
    }

    private void removeLobbyScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective != null) {
            objective.unregister();
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player.getUniqueId());
    }

    private boolean isInLobby(Player player) {
        return player.getWorld().getName().equalsIgnoreCase(LOBBY_WORLD_NAME);
    }
}