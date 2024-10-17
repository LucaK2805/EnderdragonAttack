package gamemode.enderdragonattack.Kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitItems {

    private final Map<String, List<ItemStack>> kitItems;

    public KitItems() {
        this.kitItems = new HashMap<>();
        initializeKitItems();
    }

    private void initializeKitItems() {
        // Bow Kit
        kitItems.put("Bow", Arrays.asList(
                createItem(Material.BOW, 1, "Bow Kit Bow", Enchantment.ARROW_DAMAGE, 1),
                createItem(Material.ARROW, 256)
        ));

        // Miner Kit
        kitItems.put("Miner", Arrays.asList(
                createItem(Material.DIAMOND_PICKAXE, 1, "Miner's Pickaxe", Enchantment.DIG_SPEED, 3),
                createItem(Material.IRON_SHOVEL, 1, "Miner's Shovel", Enchantment.DIG_SPEED, 2),
                createItem(Material.TORCH, 64)
        ));

        // Trader Kit
        kitItems.put("Trader", Arrays.asList(
                createItem(Material.EMERALD, 256)
        ));

        // Armorer Kit
        kitItems.put("Armorer", Arrays.asList(
                createItem(Material.IRON_HELMET, 1),
                createItem(Material.IRON_CHESTPLATE, 1),
                createItem(Material.IRON_LEGGINGS, 1),
                createItem(Material.IRON_BOOTS, 1)
        ));

        // Toolsmith Kit
        kitItems.put("Toolsmith", Arrays.asList(
                createItem(Material.IRON_AXE, 1, "Toolsmith's Axe", Enchantment.DIG_SPEED, 1),
                createItem(Material.IRON_PICKAXE, 1, "Toolsmith's Pickaxe", Enchantment.DIG_SPEED, 1),
                createItem(Material.IRON_SHOVEL, 1, "Toolsmith's Shovel", Enchantment.DIG_SPEED, 1),
                createItem(Material.IRON_HOE, 1, "Toolsmith's Hoe", Enchantment.DIG_SPEED, 1)
        ));
    }

    private ItemStack createItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    private ItemStack createItem(Material material, int amount, String name, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public List<ItemStack> getKitItems(String kitName) {
        return kitItems.getOrDefault(kitName, Collections.emptyList());
    }
}