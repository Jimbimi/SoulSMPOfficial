package net.kevarion.soulSMP.classes.reaper;

import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Reaper extends SMPClass {
    @Override
    public Component getName() {
        return Component.text("Reaper").color(TextColor.color(0xFF0000));
    }

    @Override
    public String getIdentifier() {
        return "reaper";
    }

    @Override
    public List<Component> getLore() {
        return List.of(Component.text("Reaper class").color(NamedTextColor.GRAY));
    }

    @Override
    public int getCustomModelData() {
        return 1;
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of(new LifeStealer(), new DeathsTouch(), new GraveStep());
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
