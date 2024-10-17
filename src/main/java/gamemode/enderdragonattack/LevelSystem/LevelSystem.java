package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Core;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LevelSystem {

    private static final long LEVEL_CHECK_INTERVAL = 20L; // 1 second in ticks
    private static final String LEVEL_UP_MESSAGE = ChatColor.YELLOW + "Congratulations! " + ChatColor.GRAY + "You've reached " + ChatColor.GOLD + "Level %d" + ChatColor.GRAY + "!";

    private final String prefix;
    private final PlayerDataBase playerDataBase;
    private final StatsDataBase statsDataBase;
    private final Core plugin;
    private final Map<UUID, Integer> lastKnownLevels;
    private final LevelRank levelRank;

    public LevelSystem(Core plugin, PlayerDataBase playerDataBase, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
        this.statsDataBase = statsDataBase;
        this.lastKnownLevels = new HashMap<>();
        this.levelRank = new LevelRank();

        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

        startLevelChecker();
    }

    private void startLevelChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(LevelSystem.this::checkAndUpdatePlayerRank);
            }
        }.runTaskTimer(plugin, 0L, LEVEL_CHECK_INTERVAL);
    }

    public void checkAndUpdatePlayerRank(Player player) {
        int currentLevel = getPlayerLevel(player);
        UUID playerUUID = player.getUniqueId();

        Integer lastKnownLevel = lastKnownLevels.get(playerUUID);
        if (lastKnownLevel == null || lastKnownLevel != currentLevel) {
            updatePlayerRank(player, currentLevel);

            if (lastKnownLevel != null && currentLevel > lastKnownLevel) {
                handleLevelUp(player, currentLevel);
            }

            lastKnownLevels.put(playerUUID, currentLevel);
        }
    }

    public void updatePlayerRank(Player player, int level) {
        String tabListName = levelRank.getTabListName(player, level);
        String chatName = levelRank.getFormattedName(player, level);

        player.setPlayerListName(tabListName);
        player.setDisplayName(chatName);
        player.setCustomName(chatName);
        player.setCustomNameVisible(true);
    }

    private void handleLevelUp(Player player, int newLevel) {
        player.sendMessage(prefix + String.format(LEVEL_UP_MESSAGE, newLevel));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public int getPlayerLevel(Player player) {
        int experience = playerDataBase.getPlayerExperience(player);
        return calculateLevelFromExperience(experience);
    }

    public int getOfflinePlayerLevel(String playerName) {
        int experience = statsDataBase.getPlayerExperience(playerName);
        return calculateLevelFromExperience(experience);
    }

    private int calculateLevelFromExperience(int experience) {
        int level = 0;
        while (experience >= calculateExperienceForLevel(level + 1)) {
            experience -= calculateExperienceForLevel(level + 1);
            level++;
        }
        return level;
    }

    public int calculateExperienceForLevel(int level) {
        return level * 100;
    }

    public int getExperienceForNextLevel(int currentLevel) {
        return (currentLevel + 1) * 100;
    }

    public int getTotalExperienceForLevel(int level) {
        return level * (level - 1) * 50;
    }

    public int getExperienceToNextLevel(Player player) {
        int currentExperience = playerDataBase.getPlayerExperience(player);
        return calculateExperienceToNextLevel(currentExperience);
    }

    public void addExperience(Player player, int experienceToAdd) {
        int newExperience = playerDataBase.getPlayerExperience(player) + experienceToAdd;
        playerDataBase.setPlayerExperience(player, newExperience);
        checkAndUpdatePlayerRank(player);
    }

    public int calculateExperienceToNextLevel(int currentExperience) {
        int level = calculateLevelFromExperience(currentExperience);
        return getExperienceForNextLevel(level) - (currentExperience - getTotalExperienceForLevel(level));
    }

    public int getExperienceGainedTowardsNextLevel(Player player) {
        int currentExperience = playerDataBase.getPlayerExperience(player);
        int currentLevel = calculateLevelFromExperience(currentExperience);
        return currentExperience - getTotalExperienceForLevel(currentLevel);
    }

    public List<String> getAllPlayerNames() {
        return playerDataBase.getAllPlayerNames();
    }

    public String getFormattedName(Player player) {
        return levelRank.getFormattedName(player, getPlayerLevel(player));
    }
}