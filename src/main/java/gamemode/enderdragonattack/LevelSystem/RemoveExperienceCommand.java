package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveExperienceCommand implements CommandExecutor {

    private static final String PERMISSION = "levelsystem.removeexperience";
    private static final String USAGE = "Usage: /removeExperience <playername> <amount>";
    private static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command.";
    private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player not found.";
    private static final String INVALID_AMOUNT = ChatColor.RED + "Invalid experience amount. Please enter a number.";
    private static final String SUCCESS_MESSAGE = ChatColor.GREEN + "Removed %d experience from %s";

    private final LevelSystem levelSystem;
    private final PlayerDataBase playerDataBase;

    public RemoveExperienceCommand(LevelSystem levelSystem, PlayerDataBase playerDataBase) {
        this.levelSystem = levelSystem;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + USAGE);
            return false;
        }

        String playerName = args[0];
        int experienceToRemove;

        try {
            experienceToRemove = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(INVALID_AMOUNT);
            return false;
        }

        Player targetPlayer = sender.getServer().getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage(PLAYER_NOT_FOUND);
            return false;
        }

        removeExperience(targetPlayer, experienceToRemove);
        sender.sendMessage(String.format(SUCCESS_MESSAGE, experienceToRemove, playerName));
        return true;
    }

    private void removeExperience(Player player, int experienceToRemove) {
        int currentExperience = playerDataBase.getPlayerExperience(player);
        int newExperience = Math.max(0, currentExperience - experienceToRemove);
        playerDataBase.setPlayerExperience(player, newExperience);
        levelSystem.checkAndUpdatePlayerRank(player);
    }
}