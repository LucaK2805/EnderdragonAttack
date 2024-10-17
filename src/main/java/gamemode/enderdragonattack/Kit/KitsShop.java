package gamemode.enderdragonattack.Kit;

import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class KitsShop implements Listener {
    private final PlayerDataBase playerDataBase;
    private final KitManager kitManager;
    private final Map<String, Integer> kitPrices;
    private final Map<String, Material> kitIcons;
    private final Map<String, String> kitDescriptions;
    private final String Prefix;

    public KitsShop(PlayerDataBase playerDataBase, KitManager kitManager) {
        this.playerDataBase = playerDataBase;
        this.kitManager = kitManager;
        this.kitPrices = new HashMap<>();
        this.kitIcons = new HashMap<>();
        this.kitDescriptions = new HashMap<>();
        initializeKitData();
        this.Prefix = "[" + new Gradient().generateGradient("Dragon") + ChatColor.RESET + "] ";
    }

    private void initializeKitData() {
        String[] kits = {"Bow", "Miner", "Trader", "Armorer", "Toolsmith"};
        int[] prices = {1000, 1500, 2000, 2500, 2000};
        Material[] icons = {Material.BOW, Material.DIAMOND_PICKAXE, Material.EMERALD, Material.DIAMOND_CHESTPLATE, Material.ANVIL};
        String[] descriptions = {
                "Starts with a bow and arrows",
                "Starts with better mining tools",
                "Starts with emeralds for trading",
                "Starts with better armor",
                "Starts with better tools"
        };

        for (int i = 0; i < kits.length; i++) {
            kitPrices.put(kits[i], prices[i]);
            kitIcons.put(kits[i], icons[i]);
            kitDescriptions.put(kits[i], descriptions[i]);
        }
    }

    public void openKitShop(Player player) {
        Inventory kitShop = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Kit Shop");
        int[] kitSlots = {1, 7, 13, 19, 25};
        int index = 0;
        for (Map.Entry<String, Integer> entry : kitPrices.entrySet()) {
            if (index < kitSlots.length) {
                kitShop.setItem(kitSlots[index], createKitItem(entry.getKey(), kitManager.isKitUnlockedForPlayer(player, entry.getKey()), entry.getValue()));
                index++;
            }
        }
        player.openInventory(kitShop);
    }

    private ItemStack createKitItem(String kitName, boolean isUnlocked, int price) {
        ItemStack item = new ItemStack(kitIcons.get(kitName));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((isUnlocked ? ChatColor.GREEN : ChatColor.RED) + kitName + ChatColor.GRAY + " (" + (isUnlocked ? "Unlocked" : "Not unlocked") + ")");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.WHITE + kitDescriptions.get(kitName),
                "",
                (isUnlocked ? ChatColor.GRAY : ChatColor.RED) + "Status: " + (isUnlocked ? "Unlocked" : "Not unlocked"),
                "",
                ChatColor.YELLOW + "Price: " + price + " coins"
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Kit Shop")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).split(" \\(")[0];
                if (!kitManager.isKitUnlockedForPlayer(player, kitName)) {
                    openConfirmationGUI(player, kitName, kitPrices.get(kitName));
                } else {
                    player.sendMessage(Prefix + ChatColor.RED + "You already have this kit unlocked.");
                }
            }
        } else if (event.getView().getTitle().startsWith(ChatColor.GOLD + "Confirm Purchase: ")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                String kitName = ChatColor.stripColor(event.getView().getTitle().split(": ")[1]);
                int price = kitPrices.get(kitName);
                if (clickedItem.getType() == Material.EMERALD_BLOCK) {
                    if (playerDataBase.getPlayerCoins(player) >= price) {
                        playerDataBase.setPlayerCoins(player, playerDataBase.getPlayerCoins(player) - price);
                        kitManager.unlockKitForPlayer(player, kitName);
                        player.sendMessage(Prefix + ChatColor.GREEN + "You have unlocked the " + kitName + " kit!");
                        player.closeInventory();
                    } else {
                        player.sendMessage(Prefix + ChatColor.RED + "You do not have enough coins to purchase this kit.");
                        player.closeInventory();
                    }
                } else if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                    openKitShop(player);
                }
            }
        }
    }

    private void openConfirmationGUI(Player player, String kitName, int price) {
        Inventory confirmGUI = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Confirm Purchase: " + kitName);
        confirmGUI.setItem(11, createConfirmationItem(Material.REDSTONE_BLOCK, ChatColor.RED + "Cancel Purchase",
                "Click to cancel the purchase", "and return to the shop."));
        confirmGUI.setItem(15, createConfirmationItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Confirm Purchase",
                "Click to confirm the purchase of", kitName + " for " + price + " coins."));
        player.openInventory(confirmGUI);
    }

    private ItemStack createConfirmationItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}