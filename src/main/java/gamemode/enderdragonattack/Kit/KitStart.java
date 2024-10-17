package gamemode.enderdragonattack.Kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitStart {

    private final KitManager kitManager;
    private final KitItems kitItems;

    public KitStart(KitManager kitManager, KitItems kitItems) {
        this.kitManager = kitManager;
        this.kitItems = kitItems;
    }

    public void openKitSelectionMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Select Your Kit");

        List<String> unlockedKits = getUnlockedKits(player);

        if (unlockedKits.isEmpty()) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta barrierMeta = barrier.getItemMeta();
            barrierMeta.setDisplayName(ChatColor.RED + "No Kits Available");
            barrier.setItemMeta(barrierMeta);
            inventory.setItem(13, barrier);
        } else {
            int[] kitSlots = {1, 7, 13, 19, 25}; // Same arrangement as in the shop
            String[] kitOrder = {"Bow", "Miner", "Trader", "Armorer", "Toolsmith"}; // Order of kits in the shop

            for (int i = 0; i < kitOrder.length; i++) {
                String kitName = kitOrder[i];
                if (unlockedKits.contains(kitName)) {
                    ItemStack kitItem = createKitItem(kitName);
                    inventory.setItem(kitSlots[i], kitItem);
                }
            }
        }

        player.openInventory(inventory);
    }

    private List<String> getUnlockedKits(Player player) {
        List<String> unlockedKits = new ArrayList<>();
        for (String kit : kitManager.getValidKits()) {
            if (kitManager.isKitUnlockedForPlayer(player, kit)) {
                unlockedKits.add(kit);
            }
        }
        return unlockedKits;
    }

    private ItemStack createKitItem(String kitName) {
        List<ItemStack> kitContents = kitItems.getKitItems(kitName);
        ItemStack displayItem = kitContents.isEmpty() ? new ItemStack(Material.CHEST) : kitContents.get(0).clone();

        ItemMeta meta = displayItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + kitName + " Kit");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to select this kit");
        lore.add(ChatColor.YELLOW + "Contents:");
        for (ItemStack item : kitContents) {
            lore.add(ChatColor.GRAY + "- " + item.getType().toString());
        }
        meta.setLore(lore);
        displayItem.setItemMeta(meta);

        return displayItem;
    }

    public void giveKitToPlayer(Player player, String kitName) {
        List<ItemStack> kitItems = this.kitItems.getKitItems(kitName);
        for (ItemStack item : kitItems) {
            player.getInventory().addItem(item);
        }
        player.sendMessage(ChatColor.GREEN + "You have received the " + kitName + " kit!");
    }
}