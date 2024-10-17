package gamemode.enderdragonattack.Color;

import net.md_5.bungee.api.ChatColor;
import java.awt.Color;

public class Gradient {

    private ChatColor color1 = ChatColor.BLUE;
    private ChatColor color2 = ChatColor.GREEN;

    public String generateGradient(String prefix) {
        StringBuilder gradientPrefix = new StringBuilder();
        char[] chars = prefix.toCharArray();
        float totalChars = chars.length;
        for (int i = 0; i < chars.length; i++) {
            float ratio = i / totalChars;
            ChatColor color = blendColors(color1, color2, ratio);
            gradientPrefix.append(color).append(chars[i]);
        }
        return gradientPrefix.toString();
    }

    private ChatColor blendColors(ChatColor color1, ChatColor color2, float ratio) {
        Color rgb1 = ChatColor.valueOf(color1.name()).getColor();
        Color rgb2 = ChatColor.valueOf(color2.name()).getColor();

        float[] hsb1 = Color.RGBtoHSB(rgb1.getRed(), rgb1.getGreen(), rgb1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB(rgb2.getRed(), rgb2.getGreen(), rgb2.getBlue(), null);

        float hue = interpolate(hsb1[0], hsb2[0], ratio);
        float saturation = interpolate(hsb1[1], hsb2[1], ratio);
        float brightness = interpolate(hsb1[2], hsb2[2], ratio);

        Color blendedColor = Color.getHSBColor(hue, saturation, brightness);
        return ChatColor.of(blendedColor);
    }

    private float interpolate(float a, float b, float ratio) {
        return a * (1 - ratio) + b * ratio;
    }
}
