package gamemode.enderdragonattack.Bossbar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class DragonBossBar {

    private static final String WORLD_NAME = "GameWorld";
    private static final String BOSS_BAR_TITLE = ChatColor.YELLOW + "Ender Dragon";
    private static final int UPDATE_INTERVAL = 20;
    private static final int FILL_INTERVAL = 4;
    private static final int FILL_DURATION = 100;

    private final BossBar bossBar;
    private final EnderDragon enderDragon;
    private final Plugin plugin;

    public DragonBossBar(EnderDragon enderDragon, Plugin plugin) {
        this.enderDragon = enderDragon;
        this.plugin = plugin;

        bossBar = Bukkit.createBossBar(BOSS_BAR_TITLE, BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setProgress(0.0);
        bossBar.setVisible(true);

        addOnlinePlayers();
    }

    private void addOnlinePlayers() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().getName().equals(WORLD_NAME))
                .forEach(bossBar::addPlayer);
    }

    private void updateOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getWorld().getName().equals(WORLD_NAME)) {
                if (!bossBar.getPlayers().contains(player)) {
                    bossBar.addPlayer(player);
                }
            } else {
                bossBar.removePlayer(player);
            }
        });
    }

    public void startFillingBossBar() {
        new BukkitRunnable() {
            double progress = 0.0;
            int count = 0;

            @Override
            public void run() {
                if (count < FILL_DURATION) {
                    progress = Math.min(progress + 0.01, 1.0);
                    bossBar.setProgress(progress);
                    count++;
                } else {
                    bossBar.setProgress(1.0);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, FILL_INTERVAL);
    }

    public void updateBossBar() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (enderDragon.isValid()) {
                    updateBossBarHealth();
                } else {
                    bossBar.removeAll();
                    cancel();
                }
                updateOnlinePlayers();
            }
        }.runTaskTimer(plugin, 0, UPDATE_INTERVAL);
    }

    private void updateBossBarHealth() {
        double maxHealth = enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double currentHealth = enderDragon.getHealth();
        double healthPercentage = currentHealth / maxHealth;

        bossBar.setTitle(String.format("%s - %d / %d HP", BOSS_BAR_TITLE, (int) currentHealth, (int) maxHealth));
        bossBar.setProgress(healthPercentage);
    }

    public void removeBossBar() {
        bossBar.removeAll();
    }
}