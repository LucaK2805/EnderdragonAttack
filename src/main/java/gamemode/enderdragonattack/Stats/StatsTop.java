package gamemode.enderdragonattack.Stats;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Core;
import gamemode.enderdragonattack.LevelSystem.LevelSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class StatsTop implements CommandExecutor, TabCompleter {

    private static final String USAGE = ChatColor.RED + "Usage: /top <Games|Damage|Level>";
    private static final String INVALID_STAT_TYPE = ChatColor.RED + "Invalid stat type. Use Games, Damage, or Level.";
    private static final String TOP_HEADER = ChatColor.GOLD + "=== Top 10 %s ===";
    private static final String FOOTER = ChatColor.GOLD + "--------------------";
    private static final int TOP_LIMIT = 10;

    private final Core plugin;
    private final StatsDataBase statsDataBase;
    private final LevelSystem levelSystem;
    private final String prefix;
    private final List<String> STAT_TYPES = Arrays.asList("Games", "Damage", "Level");

    public StatsTop(Core plugin, StatsDataBase statsDataBase, LevelSystem levelSystem) {
        this.plugin = plugin;
        this.statsDataBase = statsDataBase;
        this.levelSystem = levelSystem;
        this.prefix = createPrefix();
    }

    private String createPrefix() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        return ChatColor.GRAY + "[" + gradientPrefix + ChatColor.GRAY + "] " + ChatColor.RESET;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        statsDataBase.onReload();
        if (args.length != 1) {
            sender.sendMessage(prefix + USAGE);
            return true;
        }

        String statType = args[0].toLowerCase();
        StatType type = getStatType(statType);
        if (type == null) {
            sender.sendMessage(prefix + INVALID_STAT_TYPE);
            return true;
        }

        List<Map.Entry<String, Double>> topList = getTopList(type);
        displayTopList(sender, statType.toUpperCase(), topList);

        if (sender instanceof Player) {
            displayPlayerRank((Player) sender, topList);
        }

        return true;
    }

    private StatType getStatType(String statType) {
        switch (statType) {
            case "games": return StatType.GAMES;
            case "damage": return StatType.DAMAGE;
            case "level": return StatType.LEVEL;
            default: return null;
        }
    }

    private void displayTopList(CommandSender sender, String statType, List<Map.Entry<String, Double>> topList) {
        sender.sendMessage(" ");
        sender.sendMessage(prefix + String.format(TOP_HEADER, statType));
        sender.sendMessage(" ");
        for (int i = 0; i < Math.min(topList.size(), TOP_LIMIT); i++) {
            Map.Entry<String, Double> entry = topList.get(i);
            sender.sendMessage(formatLeaderboardEntry(i + 1, entry.getKey(), entry.getValue()));
        }
        sender.sendMessage(" ");
    }

    private void displayPlayerRank(Player player, List<Map.Entry<String, Double>> topList) {
        int playerRank = getPlayerRank(player.getName(), topList);
        if (playerRank > TOP_LIMIT) {
            Map.Entry<String, Double> playerEntry = topList.get(playerRank - 1);
            player.sendMessage(formatLeaderboardEntry(playerRank, playerEntry.getKey(), playerEntry.getValue()));
        }
        player.sendMessage(prefix + FOOTER);
    }

    private String formatLeaderboardEntry(int rank, String playerName, double value) {
        return prefix + String.format("%s%d. %s%s: %s%.0f",
                ChatColor.YELLOW, rank,
                ChatColor.WHITE, playerName,
                ChatColor.GREEN, value);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return STAT_TYPES.stream()
                    .filter(type -> type.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<Map.Entry<String, Double>> getTopList(StatType statType) {
        Map<String, Double> statsMap = new HashMap<>();
        List<String> playerNames = (statType == StatType.LEVEL) ? levelSystem.getAllPlayerNames() : statsDataBase.getAllPlayerNames();

        for (String playerName : playerNames) {
            double value = getValue(statType, playerName);
            statsMap.put(playerName, value);
        }

        return statsMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    private double getValue(StatType statType, String playerName) {
        switch (statType) {
            case GAMES: return statsDataBase.getGamesPlayed(playerName);
            case DAMAGE: return statsDataBase.getTotalDamage(playerName);
            case LEVEL: return levelSystem.getOfflinePlayerLevel(playerName);
            default: return 0;
        }
    }

    private int getPlayerRank(String playerName, List<Map.Entry<String, Double>> topList) {
        for (int i = 0; i < topList.size(); i++) {
            if (topList.get(i).getKey().equals(playerName)) {
                return i + 1;
            }
        }
        return topList.size() + 1;
    }

    private enum StatType {
        GAMES, DAMAGE, LEVEL
    }
}