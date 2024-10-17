package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockBreakProtection implements Listener {

    private static final String PROTECTED_WORLD = "GameWorld";
    private static final int PROTECTION_RADIUS = 30;
    private static final Set<Material> PROTECTED_MATERIALS = new HashSet<>(Arrays.asList(
            Material.TORCH,
            Material.END_STONE,
            Material.BEDROCK
    ));

    private final String prefix;

    public BlockBreakProtection() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        World world = block.getWorld();

        if (player.isOp() || !world.getName().equals(PROTECTED_WORLD)) {
            return;
        }

        if (isProtectedBlock(block)) {
            event.setCancelled(true);
            player.sendMessage(prefix + ChatColor.RED + "You cannot break this block here!");
        }
    }

    private boolean isProtectedBlock(Block block) {
        return isWithinProtectionRadius(block) && PROTECTED_MATERIALS.contains(block.getType());
    }

    private boolean isWithinProtectionRadius(Block block) {
        int blockX = block.getX();
        int blockZ = block.getZ();
        double distance = Math.sqrt(blockX * blockX + blockZ * blockZ);
        return distance <= PROTECTION_RADIUS;
    }
}