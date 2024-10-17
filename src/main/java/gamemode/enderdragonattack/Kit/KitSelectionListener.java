package gamemode.enderdragonattack.Kit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KitSelectionListener implements Listener {
    private final KitStart kitStart;

    public KitSelectionListener(KitStart kitStart) {
        this.kitStart = kitStart;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Select Your Kit")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                Player player = (Player) event.getWhoClicked();
                String kitName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).replace(" Kit", "");
                kitStart.giveKitToPlayer(player, kitName);
                player.closeInventory();
            }
        }
    }
}