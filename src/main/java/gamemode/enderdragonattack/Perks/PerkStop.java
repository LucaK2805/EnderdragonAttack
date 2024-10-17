package gamemode.enderdragonattack.Perks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumMap;
import java.util.Map;

public class PerkStop {

    private static final Map<PerkType, PotionEffectType> PERK_EFFECTS = new EnumMap<>(PerkType.class);

    static {
        PERK_EFFECTS.put(PerkType.NIGHTVISION, PotionEffectType.NIGHT_VISION);
        PERK_EFFECTS.put(PerkType.JUMPBOOST, PotionEffectType.JUMP);
        PERK_EFFECTS.put(PerkType.SPEED, PotionEffectType.SPEED);
        PERK_EFFECTS.put(PerkType.HASTE, PotionEffectType.FAST_DIGGING);
        PERK_EFFECTS.put(PerkType.SLOWFALLING, PotionEffectType.SLOW_FALLING);
    }

    private final PerkManager perkManager;

    public PerkStop(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    public void removePerksFromAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::removePerksFromPlayer);
    }

    public void removePerksFromPlayer(Player player) {
        for (PerkType perkType : PerkType.values()) {
            if (perkManager.isPerkUnlockedForPlayer(player, perkType.name())) {
                removePerkEffect(player, perkType);
            }
        }
    }

    private void removePerkEffect(Player player, PerkType perkType) {
        PotionEffectType effectType = PERK_EFFECTS.get(perkType);
        if (effectType != null) {
            player.removePotionEffect(effectType);
        }
    }

    private enum PerkType {
        NIGHTVISION, JUMPBOOST, SPEED, HASTE, SLOWFALLING
    }
}