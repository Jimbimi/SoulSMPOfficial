package net.kevarion.soulSMP.manager.component;

import net.kevarion.soulSMP.SoulSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class Ability {
    protected boolean active = false;

    public abstract Component getName();
    public abstract String getIdentifier();

    public abstract int getCooldown();
    public abstract Action getAction();
    public abstract void activate(Player player);

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setUnlocked(Player player, boolean unlocked) {
        String path = "players." + player.getUniqueId() + ".abilities." + getIdentifier();
        SoulSMP.getDataManager().getPlayerDataConfig().set(path, unlocked);
        SoulSMP.getDataManager().savePlayerDataConfig();
    }

    public boolean isUnlocked(Player player) {
        String path = "players." + player.getUniqueId() + ".abilities." + getIdentifier();
        return SoulSMP.getDataManager().getPlayerDataConfig().getBoolean(path, false);
    }

    public enum Action {
        FIRST, SECOND, THIRD
    }
}
