package net.kevarion.soulSMP.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class CC {
    public static Component translate(String string, NamedTextColor color) {
        return Component.text(string).color(color);
    }

    public static Component translateHex(String string, int hexCode) {
        return Component.text(string).color(TextColor.color(hexCode));
    }
}
