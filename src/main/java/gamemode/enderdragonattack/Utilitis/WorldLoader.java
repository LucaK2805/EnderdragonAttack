package gamemode.enderdragonattack.Utilitis;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class WorldLoader {

    private static final String WORLD_LOADED = "World '%s' has been loaded.";
    private static final String WORLD_ALREADY_LOADED = "World '%s' is already loaded.";
    private static final String WORLD_UNLOADED = "World '%s' has been unloaded.";
    private static final String WORLD_NOT_LOADED = "World '%s' is not loaded.";
    private static final String WORLD_LOAD_ERROR = "Error loading world '%s': %s";
    private static final String WORLD_UNLOAD_ERROR = "Error unloading world '%s': %s";

    private final JavaPlugin plugin;

    public WorldLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean loadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            try {
                Bukkit.createWorld(new WorldCreator(worldName));
                plugin.getLogger().info(String.format(WORLD_LOADED, worldName));
                return true;
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, String.format(WORLD_LOAD_ERROR, worldName, e.getMessage()), e);
                return false;
            }
        } else {
            plugin.getLogger().info(String.format(WORLD_ALREADY_LOADED, worldName));
            return true;
        }
    }

    public boolean unloadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            try {
                boolean success = Bukkit.unloadWorld(world, false);
                if (success) {
                    plugin.getLogger().info(String.format(WORLD_UNLOADED, worldName));
                    return true;
                } else {
                    plugin.getLogger().warning(String.format(WORLD_UNLOAD_ERROR, worldName, "Unload operation failed"));
                    return false;
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, String.format(WORLD_UNLOAD_ERROR, worldName, e.getMessage()), e);
                return false;
            }
        } else {
            plugin.getLogger().info(String.format(WORLD_NOT_LOADED, worldName));
            return false;
        }
    }
}