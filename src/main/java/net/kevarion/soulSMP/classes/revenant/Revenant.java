package net.kevarion.soulSMP.classes.revenant;

import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Revenant extends SMPClass {
    @Override
    public Component getName() {
        return Component.text("Revenant", TextColor.color(0x5bc764));
    }

    @Override
    public String getIdentifier() {
        return "revenant";
    }

    @Override
    public List<Component> getLore() {
        return List.of(Component.text("Revenant Class", NamedTextColor.GRAY));
    }

    @Override
    public int getCustomModelData() {
        return 4;
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of();
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();


/*
        String displayName = LegacyComponentSerializer.legacySection().serialize(getName());
*/
        meta.displayName(getName());
        meta.setCustomModelData(getCustomModelData());
        item.setItemMeta(meta);

        return item;
    }
}
