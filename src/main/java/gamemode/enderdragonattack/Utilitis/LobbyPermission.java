package gamemode.enderdragonattack.Utilitis;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.EnumSet;
import java.util.Set;

public class LobbyPermission implements Listener {

    private static final String LOBBY_WORLD_NAME = "Lobby";
    private static final String NO_BREAK_MESSAGE = ChatColor.RED + "You cannot break blocks here!";
    private static final String NO_PLACE_MESSAGE = ChatColor.RED + "You cannot place blocks here!";
    private static final String NO_INTERACT_MESSAGE = ChatColor.RED + "You cannot interact with this!";

    private final String prefix;
    private final Set<Material> interactableMaterials;

    public LobbyPermission() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
        this.interactableMaterials = initializeInteractableMaterials();
    }

    private Set<Material> initializeInteractableMaterials() {
        return EnumSet.of(
                Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR,
                Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.CRIMSON_DOOR, Material.WARPED_DOOR,
                Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR,
                Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR
        );
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isInLobby(event.getDamager().getWorld()) && event.getDamager() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockEvent(event.getPlayer(), event, NO_BREAK_MESSAGE);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBlockEvent(event.getPlayer(), event, NO_PLACE_MESSAGE);
    }

    private void handleBlockEvent(Player player, org.bukkit.event.Cancellable event, String message) {
        if (isInLobby(player.getWorld()) && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(prefix + message);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        clearInventoryIfInLobby(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        clearInventoryIfInLobby(event.getPlayer());
    }

    private void clearInventoryIfInLobby(Player player) {
        if (isInLobby(player.getWorld())) {
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null && isInLobby(block.getWorld()) && !player.isOp() && interactableMaterials.contains(block.getType())) {
            event.setCancelled(true);
            player.sendMessage(prefix + NO_INTERACT_MESSAGE);
        }
    }

    private boolean isInLobby(World world) {
        return world.getName().equalsIgnoreCase(LOBBY_WORLD_NAME);
    }
}