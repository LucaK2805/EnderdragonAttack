package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathListener implements Listener {

    private static final String GAME_WORLD_NAME = "GameWorld";
    private static final String LOBBY_WORLD_NAME = "Lobby";
    private static final long RESPAWN_DELAY = 1L;

    private final JavaPlugin plugin;
    private final String prefix;

    public DeathListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World playerWorld = player.getWorld();

        if (isGameWorld(playerWorld)) {
            handleGameWorldDeath(event, player);
        }

        respawnPlayerInLobby(player);
    }

    private boolean isGameWorld(World world) {
        return GAME_WORLD_NAME.equals(world.getName());
    }

    private void handleGameWorldDeath(PlayerDeathEvent event, Player player) {
        event.setDeathMessage(null);
        String customDeathMessage = createCustomDeathMessage(player);
        broadcastDeathMessage(player.getWorld(), customDeathMessage);
    }

    private String createCustomDeathMessage(Player player) {
        return prefix + ChatColor.RED + "The player " + ChatColor.GRAY + player.getName() +
                ChatColor.RED + " was eliminated from the game!";
    }

    private void broadcastDeathMessage(World world, String message) {
        world.getPlayers().forEach(player -> player.sendMessage(message));
    }

    private void respawnPlayerInLobby(Player player) {
        World lobbyWorld = Bukkit.getWorld(LOBBY_WORLD_NAME);
        if (lobbyWorld != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.spigot().respawn();
                player.teleport(lobbyWorld.getSpawnLocation());
            }, RESPAWN_DELAY);
        }
    }
}