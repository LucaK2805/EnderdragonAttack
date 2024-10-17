package gamemode.enderdragonattack.Start_Stop;

import gamemode.enderdragonattack.CoinSystem.DamageTracker;
import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.plugin.java.JavaPlugin;

public class Stop {

    private final JavaPlugin plugin;
    private final DamageTracker damageTracker;

    public Stop(JavaPlugin plugin) {
        this.plugin = plugin;
        this.damageTracker = ((Core)plugin).getDamageTracker();
    }

    public void stopGame() {
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

        Start.setGameRunning(false);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof EnderDragon) {
                    entity.remove();
                }
            }
        }

        World gameWorld = Bukkit.getWorld("GameWorld");
        World lobbyWorld = Bukkit.getWorld("lobby");
        if (gameWorld != null && lobbyWorld != null) {
            for (Player player : gameWorld.getPlayers()) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }

                player.teleport(new org.bukkit.Location(lobbyWorld, -1, 118, 0));
            }
        }
    }
}