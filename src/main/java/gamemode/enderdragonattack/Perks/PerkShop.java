package gamemode.enderdragonattack.Perks;

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

public class PerkShop implements Listener {
    private static final String SHOP_TITLE = ChatColor.GOLD + "Perk Shop";
    private static final String CONFIRM_TITLE_PREFIX = ChatColor.GOLD + "Confirm Purchase: ";
    private static final int SHOP_SIZE = 27;
    private static final int[] PERK_SLOTS = {1, 7, 13, 19, 25};

    private final PlayerDataBase playerDataBase;
    private final PerkManager perkManager;
    private final Map<String, PerkInfo> perkData;
    private final String prefix;

    public PerkShop(PlayerDataBase playerDataBase, PerkManager perkManager) {
        this.playerDataBase = playerDataBase;
        this.perkManager = perkManager;
        this.perkData = initializePerkData();
        this.prefix = "[" + new Gradient().generateGradient("Dragon") + ChatColor.RESET + "] ";
    }

    private Map<String, PerkInfo> initializePerkData() {
        Map<String, PerkInfo> data = new HashMap<>();
        data.put("Nightvision", new PerkInfo(800, Material.ENDER_PEARL, "Gives you night vision\nwhile the game is running"));
        data.put("Jumpboost", new PerkInfo(2000, Material.POTION, "Gives you increased jump height\nwhile the game is running"));
        data.put("Speed", new PerkInfo(1500, Material.DIAMOND_BOOTS, "Gives you increased movement speed\nwhile the game is running"));
        data.put("Haste", new PerkInfo(2500, Material.DIAMOND_PICKAXE, "Gives you increased mining speed\nwhile the game is running"));
        data.put("Slowfalling", new PerkInfo(3000, Material.FEATHER, "Gives you reduced fall speed\nwhile the game is running"));
        return data;
    }

    public void openPerkShop(Player player) {
        Inventory perkShop = Bukkit.createInventory(null, SHOP_SIZE, SHOP_TITLE);
        int index = 0;
        for (Map.Entry<String, PerkInfo> entry : perkData.entrySet()) {
            if (index < PERK_SLOTS.length) {
                perkShop.setItem(PERK_SLOTS[index], createPerkItem(entry.getKey(), player));
                index++;
            }
        }
        player.openInventory(perkShop);
    }

    private ItemStack createPerkItem(String perkName, Player player) {
        PerkInfo info = perkData.get(perkName);
        boolean isUnlocked = perkManager.isPerkUnlockedForPlayer(player, perkName);
        ItemStack item = new ItemStack(info.icon);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).clearCustomEffects();
        }
        meta.setDisplayName((isUnlocked ? ChatColor.GREEN : ChatColor.RED) + perkName + ChatColor.GRAY + " (" + (isUnlocked ? "Unlocked" : "Not unlocked") + ")");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.WHITE + info.description,
                "",
                (isUnlocked ? ChatColor.GRAY : ChatColor.RED) + "Status: " + (isUnlocked ? "Unlocked" : "Not unlocked"),
                "",
                ChatColor.YELLOW + "Price: " + info.price + " coins"
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(SHOP_TITLE)) {
            handleShopClick(event);
        } else if (title.startsWith(CONFIRM_TITLE_PREFIX)) {
            handleConfirmationClick(event);
        }
    }

    private void handleShopClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
            String perkName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).split(" \\(")[0];
            if (!perkManager.isPerkUnlockedForPlayer(player, perkName)) {
                openConfirmationGUI(player, perkName);
            } else {
                player.sendMessage(prefix + ChatColor.RED + "You already have this perk unlocked.");
            }
        }
    }

    private void handleConfirmationClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
            String perkName = ChatColor.stripColor(event.getView().getTitle().split(": ")[1]);
            int price = perkData.get(perkName).price;
            if (clickedItem.getType() == Material.EMERALD_BLOCK) {
                purchasePerk(player, perkName, price);
            } else if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                openPerkShop(player);
            }
        }
    }

    private void purchasePerk(Player player, String perkName, int price) {
        if (playerDataBase.getPlayerCoins(player) >= price) {
            playerDataBase.setPlayerCoins(player, playerDataBase.getPlayerCoins(player) - price);
            perkManager.unlockPerkForPlayer(player, perkName);
            player.sendMessage(prefix + ChatColor.GREEN + "You have unlocked the " + perkName + " perk!");
            player.closeInventory();
        } else {
            player.sendMessage(prefix + ChatColor.RED + "You do not have enough coins to purchase this perk.");
            player.closeInventory();
        }
    }

    private void openConfirmationGUI(Player player, String perkName) {
        Inventory confirmGUI = Bukkit.createInventory(null, SHOP_SIZE, CONFIRM_TITLE_PREFIX + perkName);
        confirmGUI.setItem(11, createConfirmationItem(Material.REDSTONE_BLOCK, ChatColor.RED + "Cancel Purchase",
                "Click to cancel the purchase", "and return to the shop."));
        confirmGUI.setItem(15, createConfirmationItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Confirm Purchase",
                "Click to confirm the purchase of", perkName + " for " + perkData.get(perkName).price + " coins."));
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

    private static class PerkInfo {
        final int price;
        final Material icon;
        final String description;

        PerkInfo(int price, Material icon, String description) {
            this.price = price;
            this.icon = icon;
            this.description = description;
        }
    }
}