package gamemode.enderdragonattack.Config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataBase {

    private static final String FILE_NAME = "players.yml";
    private static final String PLAYERNAME_KEY = "Playername";
    private static final String LEVEL_KEY = "level";
    private static final String EXPERIENCE_KEY = "experience";
    private static final String COINS_KEY = "coins";
    private static final String PLAYTIME_KEY = "playtime";

    private final Plugin plugin;
    private final File file;
    private FileConfiguration config;

    public PlayerDataBase(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILE_NAME);
        createFileIfNotExists();
        reloadConfig();
    }

    private void createFileIfNotExists() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create players.yml file: " + e.getMessage());
            }
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.yml file: " + e.getMessage());
        }
    }

    public List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (String key : config.getKeys(false)) {
            String playerName = config.getString(key + "." + PLAYERNAME_KEY);
            if (playerName != null) {
                playerNames.add(playerName);
            }
        }
        return playerNames;
    }

    public int getPlayerLevel(Player player) {
        return config.getInt(getPlayerPath(player, LEVEL_KEY), 1);
    }

    public void setPlayerLevel(Player player, int level) {
        config.set(getPlayerPath(player, LEVEL_KEY), Math.max(1, level));
        save();
    }

    public void addPlayerLevel(Player player, int levelsToAdd) {
        setPlayerLevel(player, getPlayerLevel(player) + levelsToAdd);
    }

    public int getPlayerExperience(Player player) {
        return config.getInt(getPlayerPath(player, EXPERIENCE_KEY), 100);
    }

    public void setPlayerExperience(Player player, int experience) {
        config.set(getPlayerPath(player, EXPERIENCE_KEY), experience);
        save();
    }

    public void addPlayerExperience(Player player, int experienceToAdd) {
        setPlayerExperience(player, getPlayerExperience(player) + experienceToAdd);
    }

    public int getPlayerCoins(Player player) {
        return config.getInt(getPlayerPath(player, COINS_KEY), 100);
    }

    public void setPlayerCoins(Player player, int coins) {
        config.set(getPlayerPath(player, COINS_KEY), coins);
        save();
    }

    public void addPlayerCoins(Player player, int coinsToAdd) {
        setPlayerCoins(player, getPlayerCoins(player) + coinsToAdd);
    }

    public long getPlayerPlaytime(Player player) {
        return config.getLong(getPlayerPath(player, PLAYTIME_KEY), 0);
    }

    public void setPlayerPlaytime(Player player, long playtime) {
        config.set(getPlayerPath(player, PLAYTIME_KEY), playtime);
        save();
    }

    public void addNewPlayer(Player player) {
        String playerPath = player.getUniqueId().toString();
        if (!config.contains(playerPath)) {
            config.set(getPlayerPath(player, PLAYERNAME_KEY), player.getName());
            config.set(getPlayerPath(player, EXPERIENCE_KEY), 100);
            config.set(getPlayerPath(player, COINS_KEY), 100);
            config.set(getPlayerPath(player, PLAYTIME_KEY), 0);
            save();
        }
    }

    private void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    private String getPlayerPath(Player player, String key) {
        return player.getUniqueId().toString() + "." + key;
    }
}