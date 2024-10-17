package gamemode.enderdragonattack.Perks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkCommand implements CommandExecutor {

    private static final String PLAYER_ONLY_MESSAGE = ChatColor.RED + "This command can only be used by players.";

    private final PerkShop perkShop;

    public PerkCommand(PerkShop perkShop) {
        this.perkShop = perkShop;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLAYER_ONLY_MESSAGE);
            return true;
        }

        Player player = (Player) sender;
        perkShop.openPerkShop(player);
        return true;
    }
}