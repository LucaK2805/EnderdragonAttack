package gamemode.enderdragonattack.LevelSystem;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class LevelRank {

    private static final String LEVEL_FORMAT = " (Lv.%d)";
    private static final String TAB_LIST_FORMAT = ChatColor.GREEN + "[%d] %s";

    public String getFormattedName(Player player, int level) {
        String playerName = sanitizePlayerName(player.getName());
        return playerName + String.format(LEVEL_FORMAT, level);
    }

    public String getTabListName(Player player, int level) {
        return String.format(TAB_LIST_FORMAT, level, player.getName());
    }

    private String sanitizePlayerName(String playerName) {
        return playerName.replaceAll("[<>]", "");
    }
}