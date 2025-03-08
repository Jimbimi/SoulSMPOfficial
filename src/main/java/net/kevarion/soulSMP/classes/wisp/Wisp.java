package net.kevarion.soulSMP.classes.wisp;

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

public class Wisp extends SMPClass {
    @Override
    public Component getName() {
        return Component.text("Wisp", TextColor.color(0x7ed3ed));
    }

    @Override
    public String getIdentifier() {
        return "wisp";
    }

    @Override
    public List<Component> getLore() {
        return List.of(Component.text("Wisp Class", NamedTextColor.GRAY));
    }

    @Override
    public int getCustomModelData() {
        return 2;
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of(new WispSwarm());
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
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
