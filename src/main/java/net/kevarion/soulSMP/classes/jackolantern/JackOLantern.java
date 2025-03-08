package net.kevarion.soulSMP.classes.jackolantern;

import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        return List.of(new PumpkinBomb(), new CreepingVines(), new JacksGrin());
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(getName());
        meta.setCustomModelData(getCustomModelData());
        item.setItemMeta(meta);

        return item;
    }
}
