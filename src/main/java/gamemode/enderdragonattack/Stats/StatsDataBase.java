package gamemode.enderdragonattack.Stats;

import gamemode.enderdragonattack.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class StatsDataBase implements Listener {

    private static final String STATS_FILE_NAME = "stats.yml";
    private static final String PLAYERS_FILE_NAME = "players.yml";

    private final Core plugin;
    private File statsFile;
    private FileConfiguration statsConfig;
    private File playerFile;
    private FileConfiguration playerConfig;

    public StatsDataBase(Core plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), STATS_FILE_NAME);
        this.playerFile = new File(plugin.getDataFolder(), PLAYERS_FILE_NAME);
        loadConfigs();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reloadStats() {
        loadConfigs();
    }

    private void loadConfigs() {
        ensureFileExists(statsFile, STATS_FILE_NAME);
        ensureFileExists(playerFile, PLAYERS_FILE_NAME);

        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }

    private void ensureFileExists(File file, String resourceName) {
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        reloadStats();
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        statsConfig.set(uuid + ".Playername", player.getName());
        if (!statsConfig.contains(uuid + ".GamesPlayed")) {
            statsConfig.set(uuid + ".GamesPlayed", 0);
        }
        if (!statsConfig.contains(uuid + ".TotalDamage")) {
            statsConfig.set(uuid + ".TotalDamage", 0.0);
        }
        saveStatsConfig();
    }

    public void incrementGamesPlayed(Player player) {
        reloadStats();
        String uuid = player.getUniqueId().toString();
        int currentGames = statsConfig.getInt(uuid + ".GamesPlayed", 0);
        statsConfig.set(uuid + ".GamesPlayed", currentGames + 1);
        saveStatsConfig();
    }

    public void addTotalDamage(Player player, double damage) {
        String uuid = player.getUniqueId().toString();
        double currentDamage = statsConfig.getDouble(uuid + ".TotalDamage", 0.0);
        statsConfig.set(uuid + ".TotalDamage", currentDamage + damage);
        saveStatsConfig();
    }

    public int getGamesPlayed(UUID playerUUID) {
        return statsConfig.getInt(playerUUID.toString() + ".GamesPlayed", 0);
    }

    public double getTotalDamage(UUID playerUUID) {
        return statsConfig.getDouble(playerUUID.toString() + ".TotalDamage", 0.0);
    }

    public List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (String key : statsConfig.getKeys(false)) {
            String playerName = statsConfig.getString(key + ".Playername");
            if (playerName != null) {
                playerNames.add(playerName);
            }
        }
        return playerNames;
    }

    public int getPlayerExperience(String playerName) {
        reloadStats();
        for (String key : playerConfig.getKeys(false)) {
            if (playerName.equalsIgnoreCase(playerConfig.getString(key + ".Playername"))) {
                return playerConfig.getInt(key + ".experience", 100);
            }
        }
        return 0;
    }

    public int getGamesPlayed(String playerName) {
        return getStatForPlayerName(playerName, "GamesPlayed", 0);
    }

    public double getTotalDamage(String playerName) {
        return getStatForPlayerName(playerName, "TotalDamage", 0.0);
    }

    private <T> T getStatForPlayerName(String playerName, String statName, T defaultValue) {
        for (String key : statsConfig.getKeys(false)) {
            if (playerName.equalsIgnoreCase(statsConfig.getString(key + ".Playername"))) {
                return (T) statsConfig.get(key + "." + statName, defaultValue);
            }
        }
        return defaultValue;
    }

    public void onReload() {
        loadConfigs();
    }

    private void saveStatsConfig() {
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save stats config to " + statsFile, e);
        }
    }
}