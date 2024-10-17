package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumMap;
import java.util.Map;

public class PerkStart implements Listener {

    private static final String GAME_WORLD_NAME = "GameWorld";
    private static final Map<PerkType, PotionEffectType> PERK_EFFECTS = new EnumMap<>(PerkType.class);

    static {
        PERK_EFFECTS.put(PerkType.NIGHTVISION, PotionEffectType.NIGHT_VISION);
        PERK_EFFECTS.put(PerkType.JUMPBOOST, PotionEffectType.JUMP);
        PERK_EFFECTS.put(PerkType.SPEED, PotionEffectType.SPEED);
        PERK_EFFECTS.put(PerkType.HASTE, PotionEffectType.FAST_DIGGING);
        PERK_EFFECTS.put(PerkType.SLOWFALLING, PotionEffectType.SLOW_FALLING);
    }

    private final PerkManager perkManager;

    public PerkStart(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    public void applyPerksToAllPlayers() {
        World gameWorld = Bukkit.getWorld(GAME_WORLD_NAME);
        if (gameWorld != null) {
            gameWorld.getPlayers().forEach(this::applyPerksToPlayer);
        }
    }

    public void applyPerksToPlayer(Player player) {
        for (PerkType perkType : PerkType.values()) {
            if (perkManager.isPerkUnlockedForPlayer(player, perkType.name())) {
                applyPerkEffect(player, perkType);
            }
        }
    }

    private void applyPerkEffect(Player player, PerkType perkType) {
        PotionEffectType effectType = PERK_EFFECTS.get(perkType);
        if (effectType != null) {
            int amplifier = perkType == PerkType.SPEED || perkType == PerkType.HASTE ? 1 : 0;
            player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, amplifier, true, false));
        }
    }

    private enum PerkType {
        NIGHTVISION, JUMPBOOST, SPEED, HASTE, SLOWFALLING
    }
}