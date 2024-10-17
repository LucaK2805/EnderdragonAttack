package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceProtection implements Listener {

    private static final String PROTECTED_WORLD = "GameWorld";
    private static final int PROTECTION_RADIUS = 30;

    private final String prefix;

    public BlockPlaceProtection() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();

        if (player.isOp() || !world.getName().equals(PROTECTED_WORLD)) {
            return;
        }

        if (isWithinProtectionRadius(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(prefix + ChatColor.RED + "You cannot place blocks here!");
        }
    }

    private boolean isWithinProtectionRadius(Location location) {
        int blockX = location.getBlockX();
        int blockZ = location.getBlockZ();
        double distance = Math.sqrt(blockX * blockX + blockZ * blockZ);
        return distance <= PROTECTION_RADIUS;
    }
}