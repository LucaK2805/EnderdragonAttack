package gamemode.enderdragonattack.Enderdragon;

import gamemode.enderdragonattack.Bossbar.DragonBossBar;
import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonSpawner {

    private static final String GAME_WORLD_NAME = "GameWorld";
    private static final int SPAWN_HEIGHT_OFFSET = 100;
    private static final int COUNTDOWN_DURATION = 20;
    private static final int DRAGON_ACTIVATION_DELAY = 20 * 20; // 20 seconds in ticks
    private static final double WORLD_BORDER_SIZE = 200.0;

    private static EnderDragon enderDragon;
    private static DragonBossBar bossBar;

    public static EnderDragon getEnderDragon() {
        return enderDragon;
    }

    public static DragonBossBar getBossBar() {
        return bossBar;
    }

    public static void spawnEnderDragon(Plugin plugin, double dragonHealth) {
        World world = Bukkit.getWorld(GAME_WORLD_NAME);
        if (world == null) {
            Bukkit.getLogger().severe("The world named '" + GAME_WORLD_NAME + "' does not exist.");
            return;
        }

        Location spawnLocation = getSpawnLocation(world);
        enderDragon = spawnDragon(world, spawnLocation, dragonHealth);
        bossBar = new DragonBossBar(enderDragon, plugin);
        bossBar.startFillingBossBar();

        startCountdown(plugin);
        scheduleDragonActivation(plugin);
        setWorldBorder(world, spawnLocation);
    }

    private static Location getSpawnLocation(World world) {
        int highestBlockYAtZeroZero = world.getHighestBlockYAt(0, 0);
        int spawnY = highestBlockYAtZeroZero + SPAWN_HEIGHT_OFFSET;
        return new Location(world, 0, spawnY, 0);
    }

    private static EnderDragon spawnDragon(World world, Location location, double health) {
        EnderDragon dragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);
        dragon.setHealth(health);
        dragon.setAI(false);
        dragon.setPhase(EnderDragon.Phase.HOVER);
        dragon.setInvulnerable(true);
        return dragon;
    }

    private static void startCountdown(Plugin plugin) {
        new BukkitRunnable() {
            int count = COUNTDOWN_DURATION;

            @Override
            public void run() {
                if (count > 0 && (count == 20 || count == 10 || count <= 5)) {
                    sendCountdownMessage(count);
                } else if (count == 0) {
                    sendActivationMessage();
                    cancel();
                }
                count--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private static void sendCountdownMessage(int seconds) {
        String message = getPrefix() + ChatColor.YELLOW + "The Dragon will be active in " + ChatColor.RED + seconds + " seconds...";
        sendMessageToWorld(message);
    }

    private static void sendActivationMessage() {
        String message = getPrefix() + ChatColor.YELLOW + "The Dragon is now active!";
        sendMessageToWorld(message);
    }

    private static String getPrefix() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        return "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    private static void sendMessageToWorld(String message) {
        World world = Bukkit.getWorld(GAME_WORLD_NAME);
        if (world != null) {
            world.getPlayers().forEach(player -> player.sendMessage(message));
        }
    }

    private static void scheduleDragonActivation(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                enderDragon.setAI(true);
                enderDragon.setInvulnerable(false);
                enderDragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
                bossBar.updateBossBar();
            }
        }.runTaskLater(plugin, DRAGON_ACTIVATION_DELAY);
    }

    private static void setWorldBorder(World world, Location center) {
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(center);
        worldBorder.setSize(WORLD_BORDER_SIZE);
        worldBorder.setDamageBuffer(0);
        worldBorder.setWarningDistance(5);
        worldBorder.setWarningTime(10);
    }
}