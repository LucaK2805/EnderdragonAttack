package gamemode.enderdragonattack.SetAddRemoveCommands;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AddCoinsCommand implements CommandExecutor {

    private static final String PERMISSION = "enderdragonattack.addcoins";
    private static final String USAGE = ChatColor.RED + "Usage: /addcoins <player> <amount>";
    private static final String PLAYER_ONLY = ChatColor.RED + "Only players can use this command.";
    private static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command.";
    private static final String INVALID_AMOUNT = ChatColor.RED + "Invalid amount. Please enter a valid number.";
    private static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player not found or not online.";
    private static final String SUCCESS_MESSAGE = ChatColor.GREEN + "Added %d coins to player %s";

    private final Plugin plugin;
    private final PlayerDataBase playerDataBase;

    public AddCoinsCommand(Plugin plugin, PlayerDataBase playerDataBase) {
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
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(INVALID_AMOUNT);
            return true;
        }

        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage(PLAYER_NOT_FOUND);
            return true;
        }

        playerDataBase.addPlayerCoins(targetPlayer, amount);
        player.sendMessage(String.format(SUCCESS_MESSAGE, amount, targetPlayer.getName()));

        return true;
    }
}