package gamemode.enderdragonattack.Utilitis;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeatherManager {

    private static final long CLEAR_WEATHER_INTERVAL = 200L; // 10 seconds in ticks
    private static final long FIXED_TIME = 1000L; // Fixed time of day

    private final Plugin plugin;

    public WeatherManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void startWeatherControl() {
        new BukkitRunnable() {
            @Override
            public void run() {
                clearWeatherForAllWorlds();
            }
        }.runTaskTimer(plugin, 0L, CLEAR_WEATHER_INTERVAL);
    }

    private void clearWeatherForAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            clearWeather(world);
            setFixedTime(world);
        }
    }

    private void clearWeather(World world) {
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(0);
    }

    private void setFixedTime(World world) {
        world.setTime(FIXED_TIME);
    }
}