package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PerkUnlockCommand implements CommandExecutor {

    private static final String USAGE = ChatColor.RED + "Usage: /perk <give|remove> <player> <perkName>";
    private static final String INVALID_PERK = ChatColor.RED + "Invalid perk. Available perks: %s";
    private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player %s not found.";
    private static final String PERK_UNLOCKED = ChatColor.GREEN + "Perk %s has been unlocked for %s.";
    private static final String PERK_REMOVED = ChatColor.GREEN + "Perk %s has been removed from %s.";
    private static final String INVALID_ACTION = ChatColor.RED + "Invalid action. Use either 'give' or 'remove'.";

    private final PerkManager perkManager;
    private final PerkStop perkStop;
    private final Set<String> validPerks;

    public PerkUnlockCommand(PerkManager perkManager, PerkStop perkStop) {
        this.perkManager = perkManager;
        this.perkStop = perkStop;
        this.validPerks = new HashSet<>(Arrays.asList("Nightvision", "Jumpboost", "Speed", "Haste", "Slowfalling"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(USAGE);
            return false;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        String perkName = args[2];

        if (!validPerks.contains(perkName)) {
            sender.sendMessage(String.format(INVALID_PERK, String.join(", ", validPerks)));
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage(String.format(PLAYER_NOT_FOUND, playerName));
            return false;
        }

        switch (action) {
            case "give":
                perkManager.unlockPerkForPlayer(targetPlayer, perkName);
                sender.sendMessage(String.format(PERK_UNLOCKED, perkName, playerName));
                break;
            case "remove":
                perkManager.removePerkFromPlayer(targetPlayer, perkName);
                sender.sendMessage(String.format(PERK_REMOVED, perkName, playerName));
                break;
            default:
                sender.sendMessage(INVALID_ACTION);
                return false;
        }

        return true;
    }
}