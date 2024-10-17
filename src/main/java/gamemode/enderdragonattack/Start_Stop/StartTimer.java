package gamemode.enderdragonattack.Start_Stop;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.GameWorld.WorldRegenerateCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StartTimer {

    private static final int COUNTDOWN_TIME = 30;
    private static final long GAME_START_DELAY = 100L;

    private final String prefix;
    private final Plugin plugin;
    private final Set<UUID> playersInLobby;
    private final Start startCommand;
    private final WorldRegenerateCommand worldRegenerateCommand;

    private boolean timerRunning;
    private BukkitRunnable countdownTask;

    public StartTimer(Plugin plugin, Start startCommand, WorldRegenerateCommand worldRegenerateCommand) {
        this.plugin = plugin;
        this.startCommand = startCommand;
        this.worldRegenerateCommand = worldRegenerateCommand;
        this.playersInLobby = new HashSet<>();
        this.timerRunning = false;

        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";
    }

    public void playerEnteredLobby(UUID playerUUID) {
        playersInLobby.add(playerUUID);
        if (!timerRunning && !playersInLobby.isEmpty()) {
            startCountdown();
        }
    }

    public void playerLeftLobby(UUID playerUUID) {
        playersInLobby.remove(playerUUID);
        resetPlayerLevel(playerUUID);
        if (playersInLobby.isEmpty()) {
            stopCountdown();
        }
    }

    private void startCountdown() {
        timerRunning = true;
        countdownTask = new BukkitRunnable() {
            int timeLeft = COUNTDOWN_TIME;

            @Override
            public void run() {
                if (playersInLobby.isEmpty()) {
                    stopCountdown();
                    return;
                }

                updatePlayerLevels(timeLeft);

                if (timeLeft <= 0) {
                    stopCountdown();
                    prepareAndStartGame();
                }

                timeLeft--;
            }
        };
        countdownTask.runTaskTimer(plugin, 0L, 20L);
    }

    private void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        timerRunning = false;
    }

    private void updatePlayerLevels(int timeLeft) {
        for (UUID playerUUID : playersInLobby) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                player.setLevel(timeLeft);
            }
        }
    }

    private void resetPlayerLevel(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.setLevel(0);
        }
    }

    private void prepareAndStartGame() {
        if (worldRegenerateCommand.resetWorld("GameWorld", "ExampleWorld", "Lobby")) {
            Bukkit.getScheduler().runTaskLater(plugin, this::startGame, GAME_START_DELAY);
        } else {
            Bukkit.getLogger().severe("World could not be reset. The game will not start.");
        }
    }

    private void startGame() {
        for (UUID playerUUID : playersInLobby) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                startCommand.addParticipant(player);
            }
        }

        if (!startCommand.getParticipants().isEmpty()) {
            startCommand.startGame();
        }

        playersInLobby.clear();
    }
}