package net.kevarion.soulSMP.manager.component;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class SMPClass {
    public abstract Component getName();
    public abstract String getIdentifier();
    public abstract List<Component> getLore();
    public abstract int getCustomModelData();
    public abstract List<Ability> getAbilities();
    public abstract ItemStack getItem();
}
