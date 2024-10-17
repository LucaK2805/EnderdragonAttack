package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddExperienceCommand implements CommandExecutor {

    private static final String PERMISSION = "levelsystem.addexperience";
    private static final String USAGE = "Usage: /addExperience <playername> <amount>";

    private final LevelSystem levelSystem;
    private final PlayerDataBase playerDataBase;

    public AddExperienceCommand(LevelSystem levelSystem, PlayerDataBase playerDataBase) {
        this.levelSystem = levelSystem;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + USAGE);
            return false;
        }

        String playerName = args[0];
        int experienceToAdd;

        try {
            experienceToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid experience amount. Please enter a number.");
            return false;
        }

        Player targetPlayer = sender.getServer().getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return false;
        }

        levelSystem.addExperience(targetPlayer, experienceToAdd);
        sender.sendMessage(ChatColor.GREEN + "Added " + experienceToAdd + " experience to " + playerName);
        return true;
    }
}