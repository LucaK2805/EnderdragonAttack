package gamemode.enderdragonattack.Kit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KitManager implements Listener {

    private final File kitFile;
    private FileConfiguration kitConfig;
    private final Set<String> validKits = new HashSet<>();

    public KitManager(File dataFolder) {
        this.kitFile = new File(dataFolder, "kits.yml");
        loadKitConfig();
        initializeValidKits();
    }

    private void loadKitConfig() {
        if (!kitFile.exists()) {
            kitFile.getParentFile().mkdirs();
            try {
                kitFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        kitConfig = YamlConfiguration.loadConfiguration(kitFile);
    }

    private void initializeValidKits() {
        validKits.add("Bow");
        validKits.add("Miner");
        validKits.add("Trader");
        validKits.add("Armorer");
        validKits.add("Toolsmith");
    }

    // Add this method to get the valid kits
    public Set<String> getValidKits() {
        return new HashSet<>(validKits);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createPlayerEntry(player);
    }

    public void createPlayerEntry(Player player) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        if (!kitConfig.contains(playerUUID.toString())) {
            kitConfig.set(playerUUID.toString() + ".Playername", playerName);
            for (String kit : validKits) {
                kitConfig.set(playerUUID.toString() + "." + kit, "");
            }
            saveKitConfig();
        }
    }

    private void saveKitConfig() {
        try {
            kitConfig.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isKitUnlockedForPlayer(Player player, String kitName) {
        UUID playerUUID = player.getUniqueId();
        return "x".equals(kitConfig.getString(playerUUID.toString() + "." + kitName));
    }

    public void unlockKitForPlayer(Player player, String kitName) {
        if (validKits.contains(kitName)) {
            UUID playerUUID = player.getUniqueId();
            kitConfig.set(playerUUID.toString() + "." + kitName, "x");
            saveKitConfig();
        }
    }
}