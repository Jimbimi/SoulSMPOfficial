package net.kevarion.soulSMP.classes.jackolantern;

import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JackOLantern extends SMPClass {
    @Override
    public Component getName() {
        return Component.text("Jack-O-Lantern", NamedTextColor.GOLD);
    }

    @Override
    public String getIdentifier() {
        return "jack-o-lantern";
    }

    @Override
    public List<Component> getLore() {
        return List.of(Component.text("JackOLantern Class!", NamedTextColor.GRAY));
    }

    @Override
    public int getCustomModelData() {
        return 3;
    }

    @Override
    public List<Ability> getAbilities() {
        return List.of();
    }

    @Override
    public ItemStack getItem() {
        return null;
    }
}
