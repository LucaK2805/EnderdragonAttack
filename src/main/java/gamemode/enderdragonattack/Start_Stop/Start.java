package gamemode.enderdragonattack.Start_Stop;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.Enderdragon.DragonSpawner;
import gamemode.enderdragonattack.GameWorld.WorldRegenerateCommand;
import gamemode.enderdragonattack.Kit.KitStart;
import gamemode.enderdragonattack.Perks.PerkStart;
import gamemode.enderdragonattack.Core;
import gamemode.enderdragonattack.Stats.StatsDataBase;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Start {

    private static final String GAME_WORLD_NAME = "GameWorld";
    private static final long PERIODIC_CHECK_INTERVAL = 20L;
    private static final int GAME_DURATION_SECONDS = 15 * 60;

    private final String prefix;
    private final JavaPlugin plugin;
    private final double defaultDragonHealth;
    private final List<Player> participants;
    private final StartTimer startTimer;
    private final PerkStart perkStart;
    private final StatsDataBase statsDataBase;

    private static boolean isGameRunning = false;
    private Stop stopInstance;

    public Start(JavaPlugin plugin, double defaultDragonHealth, PerkStart perkStart, StatsDataBase statsDataBase) {
        this.plugin = plugin;
        this.defaultDragonHealth = defaultDragonHealth;
        this.participants = new ArrayList<>();
        this.perkStart = perkStart;
        this.statsDataBase = statsDataBase;

        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

        validateDependencies();

        WorldRegenerateCommand worldRegenerateCommand = new WorldRegenerateCommand(plugin);
        this.startTimer = new StartTimer(plugin, this, worldRegenerateCommand);

        startPeriodicCheck();
    }

    private void validateDependencies() {
        if (statsDataBase == null) {
            plugin.getLogger().severe("StatsDataBase is null in Start constructor!");
        }
        if (perkStart == null) {
            plugin.getLogger().severe("PerkStart is null in Start constructor!");
        }
    }

    public void startGame() {
        plugin.getLogger().info("Starting game...");
        setGameRunning(true);

        World gameWorld = Bukkit.getWorld(GAME_WORLD_NAME);
        if (gameWorld == null) {
            plugin.getLogger().severe("The world '" + GAME_WORLD_NAME + "' is not loaded or does not exist!");
            return;
        }

        Location startLocation = gameWorld.getSpawnLocation();

        for (Player participant : participants) {
            prepareParticipant(participant, startLocation);
        }

        DragonSpawner.spawnEnderDragon(plugin, defaultDragonHealth);
        participants.clear();

        applyPerksToAllPlayers();
        initializeStopInstance();

        plugin.getLogger().info("Game started successfully.");
    }

    private void prepareParticipant(Player participant, Location startLocation) {
        participant.teleport(startLocation);
        participant.setGameMode(GameMode.SURVIVAL);
        participant.getInventory().clear();

        incrementGamesPlayed(participant);
        openKitSelectionMenu(participant);
        startTimerForPlayer(participant);
    }

    private void incrementGamesPlayed(Player participant) {
        if (statsDataBase != null) {
            try {
                statsDataBase.incrementGamesPlayed(participant);
                plugin.getLogger().info("Incremented games played for player: " + participant.getName());
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error incrementing games played for player " + participant.getName(), e);
            }
        } else {
            plugin.getLogger().severe("StatsDataBase is null when trying to increment games played for player: " + participant.getName());
        }
    }

    private void openKitSelectionMenu(Player participant) {
        if (plugin instanceof Core) {
            Core core = (Core) plugin;
            KitStart kitStart = core.getKitStart();
            if (kitStart != null) {
                kitStart.openKitSelectionMenu(participant);
            } else {
                plugin.getLogger().severe("KitStart is null when trying to open kit selection menu for player: " + participant.getName());
            }
        } else {
            plugin.getLogger().severe("Plugin is not an instance of Core. Cannot open kit selection menu.");
        }
    }

    private void startTimerForPlayer(Player player) {
        new BukkitRunnable() {
            int timeLeft = GAME_DURATION_SECONDS;

            @Override
            public void run() {
                if (!isGameRunning() || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                updatePlayerTimer(player, timeLeft);

                if (timeLeft <= 0) {
                    endGameForPlayer(player);
                    this.cancel();
                }

                timeLeft--;
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    private void updatePlayerTimer(Player player, int timeLeft) {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time left: " + ChatColor.GOLD + timeString));

        if (timeLeft <= 5 && timeLeft > 0) {
            player.sendTitle(
                    ChatColor.RED + Integer.toString(timeLeft),
                    ChatColor.GOLD + "Game ending soon!",
                    0, 20, 0
            );
        }
    }

    private void endGameForPlayer(Player player) {
        player.sendTitle(
                ChatColor.RED + "Time's up!",
                ChatColor.GOLD + "Game Over",
                0, 60, 20
        );
        if (stopInstance != null) {
            Bukkit.getScheduler().runTask(plugin, () -> stopInstance.stopGame());
        } else {
            plugin.getLogger().warning("Stop instance is null. Cannot stop the game.");
        }
    }

    private void applyPerksToAllPlayers() {
        if (perkStart != null) {
            perkStart.applyPerksToAllPlayers();
        } else {
            plugin.getLogger().severe("PerkStart is null when trying to apply perks to all players.");
        }
    }

    private void initializeStopInstance() {
        if (plugin instanceof Core) {
            Core core = (Core) plugin;
            this.stopInstance = new Stop(core);
        } else {
            plugin.getLogger().severe("Plugin is not an instance of Core. Cannot initialize Stop instance.");
        }
    }

    private void startPeriodicCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    World gameWorld = Bukkit.getWorld(GAME_WORLD_NAME);
                    if (gameWorld != null && gameWorld.getPlayers().isEmpty()) {
                        if (stopInstance != null) {
                            stopInstance.stopGame();
                        } else {
                            plugin.getLogger().warning("Stop instance is null. Cannot stop the game.");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, PERIODIC_CHECK_INTERVAL);
    }

    // Getter and setter methods...

    public static boolean isGameRunning() {
        return isGameRunning;
    }

    public static void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void addParticipant(Player player) {
        if (!participants.contains(player)) {
            participants.add(player);
        }
    }

    public void removeParticipant(Player player) {
        participants.remove(player);
    }

    public StartTimer getStartTimer() {
        return startTimer;
    }
}