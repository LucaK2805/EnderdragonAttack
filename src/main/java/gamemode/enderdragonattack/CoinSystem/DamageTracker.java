package gamemode.enderdragonattack.CoinSystem;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Config.PlayerDataBase;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class DamageTracker implements Listener {

    private static final double MAX_DRAGON_HEALTH = 200.0;
    private static final int MAX_LEADERBOARD_ENTRIES = 10;

    private final String prefix;
    private final Plugin plugin;
    private final PlayerDataBase playerDataBase;
    private final StatsDataBase statsDataBase;
    private final Map<UUID, Double> damageMap;
    private double totalDamage;

    public DamageTracker(Plugin plugin, PlayerDataBase playerDataBase, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.playerDataBase = playerDataBase;
        this.statsDataBase = statsDataBase;
        this.damageMap = new HashMap<>();
        this.totalDamage = 0.0;

        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;

        Player damager = getDamager(event);
        if (damager != null) {
            processPlayerDamage(damager, event);
        }
    }

    private Player getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }
        return null;
    }

    private void processPlayerDamage(Player player, EntityDamageByEntityEvent event) {
        double damage = Math.min(event.getDamage(), MAX_DRAGON_HEALTH - totalDamage);
        if (damage <= 0) return;

        totalDamage += damage;
        damageMap.merge(player.getUniqueId(), damage, Double::sum);
        event.setDamage(damage);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon) || damageMap.isEmpty()) return;

        plugin.getLogger().info("EnderDragon death detected");

        List<Map.Entry<UUID, Double>> sortedDamageList = getSortedDamageList();
        String leaderboard = generateLeaderboard(sortedDamageList);
        broadcastLeaderboard(leaderboard);
        processPlayerRewards(sortedDamageList);

        damageMap.clear();
        totalDamage = 0.0;
    }

    private List<Map.Entry<UUID, Double>> getSortedDamageList() {
        return damageMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    private String generateLeaderboard(List<Map.Entry<UUID, Double>> sortedDamageList) {
        StringBuilder leaderboard = new StringBuilder();
        leaderboard.append(ChatColor.GOLD).append(" \n")
                .append(prefix).append(ChatColor.GOLD).append("---------------------\n")
                .append(prefix).append(ChatColor.GOLD).append(" \n")
                .append(prefix).append(ChatColor.GOLD).append("Dragon Damage Leaderboard:\n")
                .append(prefix).append(ChatColor.GOLD).append(" \n");

        for (int i = 0; i < Math.min(sortedDamageList.size(), MAX_LEADERBOARD_ENTRIES); i++) {
            Map.Entry<UUID, Double> entry = sortedDamageList.get(i);
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                leaderboard.append(prefix).append(ChatColor.YELLOW).append(i + 1).append(". ")
                        .append(ChatColor.WHITE).append(player.getName()).append(": ")
                        .append(ChatColor.GREEN).append(Math.round(entry.getValue())).append(" damage\n");
            }
        }

        leaderboard.append(prefix).append(ChatColor.GOLD).append(" \n")
                .append(prefix).append(ChatColor.GOLD).append("---------------------\n")
                .append(ChatColor.GOLD).append(" \n");

        return leaderboard.toString();
    }

    private void broadcastLeaderboard(String leaderboard) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(leaderboard));
    }

    private void processPlayerRewards(List<Map.Entry<UUID, Double>> sortedDamageList) {
        for (int i = 0; i < sortedDamageList.size(); i++) {
            Map.Entry<UUID, Double> entry = sortedDamageList.get(i);
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;

            double damage = Math.min(entry.getValue(), MAX_DRAGON_HEALTH);
            int coinsToAdd = (int) Math.round(damage);
            int experienceToAdd = coinsToAdd;

            playerDataBase.addPlayerCoins(player, coinsToAdd);
            playerDataBase.addPlayerExperience(player, experienceToAdd);

            sendPlayerRewardMessage(player, coinsToAdd, experienceToAdd, i + 1, sortedDamageList.size());
            updatePlayerStats(player, damage);
        }
    }

    private void sendPlayerRewardMessage(Player player, int coinsToAdd, int experienceToAdd, int rank, int totalPlayers) {
        String title = ChatColor.GOLD + " #1 ";
        String subtitle = ChatColor.YELLOW + "#" + rank + ChatColor.GOLD + " out of " + ChatColor.YELLOW + totalPlayers;
        player.sendTitle(title, subtitle, 10, 70, 20);

        player.sendMessage(prefix + ChatColor.GREEN + "You dealt " + ChatColor.YELLOW + coinsToAdd + " damage" +
                ChatColor.GREEN + " and earned " + ChatColor.GOLD + coinsToAdd + " coins and " +
                experienceToAdd + " Experience!");
    }

    private void updatePlayerStats(Player player, double damage) {
        try {
            statsDataBase.addTotalDamage(player, damage);
            plugin.getLogger().info("Added total damage for player: " + player.getName() + ", damage: " + damage);
        } catch (Exception e) {
            plugin.getLogger().severe("Error adding total damage for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}