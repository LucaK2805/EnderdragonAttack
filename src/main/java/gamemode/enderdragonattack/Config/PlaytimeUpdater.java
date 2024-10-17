package gamemode.enderdragonattack.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeUpdater {

    private static final long UPDATE_INTERVAL = 1200L; // 60 seconds in ticks
    private static final long PLAYTIME_INCREMENT = 60L; // 60 seconds

    private final Plugin plugin;
    private final PlayerDataBase playerDataBase;

    public PlaytimeUpdater(Plugin plugin, PlayerDataBase playerDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
        startPlaytimeUpdater();
    }

    private void startPlaytimeUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlaytimeForAllPlayers();
            }
        }.runTaskTimer(plugin, 0L, UPDATE_INTERVAL);
    }

    private void updatePlaytimeForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerPlaytime(player);
        }
    }

    private void updatePlayerPlaytime(Player player) {
        long currentPlaytime = playerDataBase.getPlayerPlaytime(player);
        playerDataBase.setPlayerPlaytime(player, currentPlaytime + PLAYTIME_INCREMENT);
    }
}