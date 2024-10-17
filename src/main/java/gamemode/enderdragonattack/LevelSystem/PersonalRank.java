package gamemode.enderdragonattack.LevelSystem;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PersonalRank implements CommandExecutor {

    private static final String CONSOLE_ERROR = ChatColor.RED + "This command can only be executed by players.";
    private static final String RANK_INFO_HEADER = ChatColor.GOLD + "Your Rank Information:";
    private static final String LEVEL_FORMAT = ChatColor.YELLOW + "Current Level: " + ChatColor.WHITE + "%d";
    private static final String EXP_FORMAT = ChatColor.YELLOW + "Experience: " + ChatColor.AQUA + "%d" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%d";

    private final LevelSystem levelSystem;
    private final String prefix;

    public PersonalRank(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
        Gradient pluginInstance = new Gradient();
        String gradientPrefix = pluginInstance.generateGradient("Dragon");
        this.prefix = ChatColor.translateAlternateColorCodes('&', "&f[" + gradientPrefix + "&f] " + ChatColor.RESET);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + CONSOLE_ERROR);
            return true;
        }

        Player player = (Player) sender;
        int currentLevel = levelSystem.getPlayerLevel(player);
        int totalExperienceForNextLevel = levelSystem.calculateExperienceForLevel(currentLevel + 1);
        int experienceGainedTowardsNextLevel = levelSystem.getExperienceGainedTowardsNextLevel(player);

        sendRankInfo(sender, currentLevel, experienceGainedTowardsNextLevel, totalExperienceForNextLevel);

        return true;
    }

    private void sendRankInfo(CommandSender sender, int currentLevel, int experienceGained, int totalExperience) {
        sender.sendMessage("");
        sender.sendMessage(prefix + RANK_INFO_HEADER);
        sender.sendMessage("");
        sender.sendMessage(prefix + String.format(LEVEL_FORMAT, currentLevel));
        sender.sendMessage(prefix + String.format(EXP_FORMAT, experienceGained, totalExperience));
        sender.sendMessage("");
    }
}