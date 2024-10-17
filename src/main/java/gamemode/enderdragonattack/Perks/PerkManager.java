package gamemode.enderdragonattack.Perks;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PerkManager implements Listener {

    private static final String PERK_FILE_NAME = "Perk.yml";
    private static final String PLAYER_NAME_KEY = "Playername";
    private static final String PERK_UNLOCKED_VALUE = "x";
    private static final Set<String> VALID_PERKS = new HashSet<>(Arrays.asList(
            "Nightvision", "Jumpboost", "Speed", "Haste", "Slowfalling"
    ));

    private final File perkFile;
    private FileConfiguration perkConfig;
    private final PerkStop perkStop;
    private final Logger logger;

    public PerkManager(File dataFolder, PerkStop perkStop, Logger logger) {
        this.perkFile = new File(dataFolder, PERK_FILE_NAME);
        this.perkStop = perkStop;
        this.logger = logger;
        loadPerkConfig();
    }

    private void loadPerkConfig() {
        if (!perkFile.exists()) {
            createPerkFile();
        }
        perkConfig = YamlConfiguration.loadConfiguration(perkFile);
    }

    private void createPerkFile() {
        perkFile.getParentFile().mkdirs();
        try {
            perkFile.createNewFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create Perk.yml file", e);
        }
    }

    private void savePerkConfig() {
        try {
            perkConfig.save(perkFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save Perk.yml file", e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        createPlayerEntry(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        perkStop.removePerksFromPlayer(event.getEntity());
    }

    public void createPlayerEntry(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (!perkConfig.contains(playerUUID)) {
            perkConfig.set(playerUUID + "." + PLAYER_NAME_KEY, player.getName());
            for (String perk : VALID_PERKS) {
                perkConfig.set(playerUUID + "." + perk, "");
            }
            savePerkConfig();
        }
    }

    public void unlockPerkForPlayer(Player player, String perkName) {
        if (isValidPerk(perkName)) {
            unlockPerk(player, perkName);
        } else {
            player.sendMessage("The perk " + perkName + " is not valid.");
        }
    }

    public boolean isPerkUnlockedForPlayer(Player player, String perkName) {
        return isValidPerk(perkName) && isPerkUnlocked(player, perkName);
    }

    private void unlockPerk(Player player, String perkName) {
        String playerUUID = player.getUniqueId().toString();
        if (perkConfig.contains(playerUUID)) {
            perkConfig.set(playerUUID + "." + perkName, PERK_UNLOCKED_VALUE);
            savePerkConfig();
        } else {
            player.sendMessage("Your entry in the perk configuration could not be found.");
        }
    }

    public void removePerkFromPlayer(Player player, String perkName) {
        if (isValidPerk(perkName)) {
            String playerUUID = player.getUniqueId().toString();
            if (perkConfig.contains(playerUUID)) {
                perkConfig.set(playerUUID + "." + perkName, "");
                savePerkConfig();
            }
        }
    }

    private boolean isPerkUnlocked(Player player, String perkName) {
        String playerUUID = player.getUniqueId().toString();
        return PERK_UNLOCKED_VALUE.equals(perkConfig.getString(playerUUID + "." + perkName));
    }

    private boolean isValidPerk(String perkName) {
        return VALID_PERKS.contains(perkName);
    }

    public Set<String> getValidPerks() {
        return new HashSet<>(VALID_PERKS);
    }
}