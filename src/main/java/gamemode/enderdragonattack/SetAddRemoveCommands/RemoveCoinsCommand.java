package gamemode.enderdragonattack.SetAddRemoveCommands;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RemoveCoinsCommand implements CommandExecutor {

    private static final String PERMISSION = "enderdragonattack.removecoins";
    private static final String USAGE = ChatColor.RED + "Usage: /removecoins <player> <amount>";
    private static final String PLAYER_ONLY = ChatColor.RED + "Only players can use this command.";
    private static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command.";
    private static final String INVALID_AMOUNT = ChatColor.RED + "Invalid amount. Please enter a valid number.";
    private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player not found or not online.";
    private static final String NOT_ENOUGH_COINS = ChatColor.RED + "Player does not have enough coins.";
    private static final String SUCCESS_MESSAGE = ChatColor.GREEN + "Removed %d coins from player %s";

    private final Plugin plugin;
    private final PlayerDataBase playerDataBase;

    public RemoveCoinsCommand(Plugin plugin, PlayerDataBase playerDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLAYER_ONLY);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(NO_PERMISSION);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(USAGE);
            return true;
        }

        String targetPlayerName = args[0];
        int amountToRemove;

        try {
            amountToRemove = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(INVALID_AMOUNT);
            return true;
        }

        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage(PLAYER_NOT_FOUND);
            return true;
        }

        int currentCoins = playerDataBase.getPlayerCoins(targetPlayer);

        if (currentCoins < amountToRemove) {
            player.sendMessage(NOT_ENOUGH_COINS);
            return true;
        }

        playerDataBase.setPlayerCoins(targetPlayer, currentCoins - amountToRemove);
        player.sendMessage(String.format(SUCCESS_MESSAGE, amountToRemove, targetPlayer.getName()));

        return true;
    }
}