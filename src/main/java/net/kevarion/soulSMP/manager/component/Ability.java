package net.kevarion.soulSMP.manager.component;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class Ability {
    protected boolean active = false;
    protected boolean abilityUnlocked = false;

    public abstract Component getName();
    public abstract int getCooldown();
    public abstract Action getAction();
    public abstract void activate(Player player);

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setUnlocked(boolean unlocked) {
        this.abilityUnlocked = unlocked;
    }

    public boolean isUnlocked() {
        return abilityUnlocked;
    }

    public enum Action {
        FIRST, SECOND, THIRD
    }
}
