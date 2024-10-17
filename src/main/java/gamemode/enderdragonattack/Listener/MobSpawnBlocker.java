package gamemode.enderdragonattack.Listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnBlocker implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (event.getEntityType() != EntityType.ENDER_DRAGON && event.getEntityType() != EntityType.ARMOR_STAND) {
            event.setCancelled(true);
        }
    }
}
