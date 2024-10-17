package gamemode.enderdragonattack.Stats;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player not found.";
    private static final String SPECIFY_PLAYER = ChatColor.RED + "Please specify a player name.";
    private static final String STATS_HEADER = ChatColor.GOLD + "=== Statistics for %s ===";
    private static final String GAMES_PLAYED = ChatColor.YELLOW + "Games played: " + ChatColor.WHITE + "%d";
    private static final String TOTAL_DAMAGE = ChatColor.YELLOW + "Total damage: " + ChatColor.WHITE + "%.2f";

    private final Core plugin;
    private final StatsDataBase statsDataBase;
    private final String prefix;

    public StatsCommand(Core plugin, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.statsDataBase = statsDataBase;
        this.prefix = createPrefix();
    }

    private String createPrefix() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        return ChatColor.GRAY + "[" + gradientPrefix + ChatColor.GRAY + "] " + ChatColor.RESET;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        statsDataBase.reloadStats();

        OfflinePlayer targetPlayer = getTargetPlayer(sender, args);
        if (targetPlayer == null) {
            return true;
        }

        displayStats(sender, targetPlayer);
        return true;
    }

    private OfflinePlayer getTargetPlayer(CommandSender sender, String[] args) {
        if (args.length > 0) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            if (player == null || (!player.hasPlayedBefore() && !player.isOnline())) {
                sender.sendMessage(prefix + PLAYER_NOT_FOUND);
                return null;
            }
            return player;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + SPECIFY_PLAYER);
            return null;
        }

        return (Player) sender;
    }

    private void displayStats(CommandSender sender, OfflinePlayer targetPlayer) {
        UUID playerUUID = targetPlayer.getUniqueId();
        int gamesPlayed = statsDataBase.getGamesPlayed(playerUUID);
        double totalDamage = statsDataBase.getTotalDamage(playerUUID);

        sender.sendMessage(" ");
        sender.sendMessage(prefix + String.format(STATS_HEADER, targetPlayer.getName()));
        sender.sendMessage(" ");
        sender.sendMessage(prefix + String.format(GAMES_PLAYED, gamesPlayed));
        sender.sendMessage(prefix + String.format(TOTAL_DAMAGE, totalDamage));
        sender.sendMessage(" ");
    }
}