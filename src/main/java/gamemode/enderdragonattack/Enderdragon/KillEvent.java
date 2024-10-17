package gamemode.enderdragonattack.Enderdragon;

import gamemode.enderdragonattack.Start_Stop.Stop;
import gamemode.enderdragonattack.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEvent implements Listener {

    private final Core core;
    private Stop stopInstance;

    public KillEvent(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onEnderDragonKill(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }

        stopGame();
    }

    private void stopGame() {
        if (stopInstance == null) {
            initializeStopInstance();
        }

        if (stopInstance != null) {
            stopInstance.stopGame();
        } else {
            Bukkit.getLogger().severe("Failed to initialize Stop instance. Cannot stop the game.");
        }
    }

    private void initializeStopInstance() {
        this.stopInstance = new Stop(core);
    }
}