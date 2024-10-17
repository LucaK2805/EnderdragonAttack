package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import gamemode.enderdragonattack.LevelSystem.LevelRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand {

    private final LevelRank levelRank;
    private final String prefix;

    public RankCommand() {
        this.levelRank = new LevelRank();
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = ChatColor.translateAlternateColorCodes('&', "&f[" + gradientPrefix + "&f] " + ChatColor.RESET);
    }



    private String formatRankInfo(String rankName, int level, String colorCode) {
        return ChatColor.translateAlternateColorCodes('&',
                colorCode + rankName + "&r: Level " + level + "+");
    }
}