package gamemode.enderdragonattack.Kit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitStartCommand implements CommandExecutor {

    private final KitStart kitStart;

    public KitStartCommand(KitStart kitStart) {
        this.kitStart = kitStart;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        kitStart.openKitSelectionMenu(player);
        return true;
    }
}