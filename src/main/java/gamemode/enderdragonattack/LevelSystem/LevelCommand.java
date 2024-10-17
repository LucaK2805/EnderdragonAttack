package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {

    private static final String CONSOLE_ERROR = ChatColor.RED + "This command can only be run by a player or with a player name specified.";
    private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player not found or not online.";
    private static final String LEVEL_CHECK_MESSAGE = ChatColor.GRAY + "%s checked your level.";

    private final String prefix;
    private final LevelSystem levelSystem;

    public LevelCommand(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player targetPlayer = getTargetPlayer(sender, args);

        if (targetPlayer == null) {
            return true;
        }

        sendLevelInfo(sender, targetPlayer);
        notifyTargetPlayer(sender, targetPlayer);

        return true;
    }

    private Player getTargetPlayer(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + CONSOLE_ERROR);
                return null;
            }
            return (Player) sender;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(prefix + PLAYER_NOT_FOUND);
            return null;
        }
        return targetPlayer;
    }

    private void sendLevelInfo(CommandSender sender, Player targetPlayer) {
        int currentLevel = levelSystem.getPlayerLevel(targetPlayer);
        int totalExperienceForNextLevel = levelSystem.calculateExperienceForLevel(currentLevel + 1);
        int experienceGainedTowardsNextLevel = levelSystem.getExperienceGainedTowardsNextLevel(targetPlayer);
        int experienceGainedTowardsNextLevelCalculated = experienceGainedTowardsNextLevel - (currentLevel * 100);

        String message = String.format("%s%s%s is level %s%d%s with %s%d%s/%s%d%s experience for the next level.",
                prefix,
                ChatColor.YELLOW, targetPlayer.getName(),
                ChatColor.YELLOW, currentLevel, ChatColor.GRAY,
                ChatColor.YELLOW, experienceGainedTowardsNextLevelCalculated, ChatColor.RED,
                ChatColor.YELLOW, totalExperienceForNextLevel, ChatColor.GRAY);

        sender.sendMessage(message);
    }

    private void notifyTargetPlayer(CommandSender sender, Player targetPlayer) {
        if (sender != targetPlayer && targetPlayer.isOnline()) {
            targetPlayer.sendMessage(prefix + String.format(LEVEL_CHECK_MESSAGE, sender.getName()));
        }
    }
}