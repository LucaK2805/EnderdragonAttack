package gamemode.enderdragonattack.Utilitis;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTeleporter implements CommandExecutor {

    private static final String PLAYER_ONLY = ChatColor.RED + "Only players can use this command.";
    private static final String USAGE = ChatColor.RED + "Usage: /teleport <player> <world>";
    private static final String PLAYER_NOT_ONLINE = ChatColor.RED + "Player %s is not online.";
    private static final String WORLD_NOT_EXIST = ChatColor.RED + "World %s does not exist.";
    private static final String TELEPORT_SUCCESS = ChatColor.GREEN + "Teleported %s to %s.";

    private final String prefix;

    public WorldTeleporter() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + PLAYER_ONLY);
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(prefix + USAGE);
            return true;
        }

        String playerName = args[0];
        String worldName = args[1];

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(prefix + String.format(PLAYER_NOT_ONLINE, playerName));
            return true;
        }

        World targetWorld = Bukkit.getWorld(worldName);
        if (targetWorld == null) {
            player.sendMessage(prefix + String.format(WORLD_NOT_EXIST, worldName));
            return true;
        }

        targetPlayer.teleport(targetWorld.getSpawnLocation());
        player.sendMessage(prefix + String.format(TELEPORT_SUCCESS, playerName, worldName));
        return true;
    }
}