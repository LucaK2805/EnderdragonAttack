package gamemode.enderdragonattack.Start_Stop;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldChecker {

    private final JavaPlugin plugin;
    private static final String TARGET_WORLD_NAME = "GameWorld";

    public WorldChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startChecking() {
        new WorldCheckerTask().runTaskTimer(plugin, 0L, 20L); // Every 20 ticks (~1 second)
    }

    private class WorldCheckerTask extends BukkitRunnable {
        @Override
        public void run() {
            World targetWorld = Bukkit.getWorld(TARGET_WORLD_NAME);

            if (targetWorld == null) {
                plugin.getLogger().warning("World '" + TARGET_WORLD_NAME + "' was not found!");
                return;
            }
        }
    }
}