package net.kevarion.soulSMP.classes.revenant;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RevenantRage extends Ability implements Listener {
    private final Set<UUID> activePlayer = new HashSet<>();

    @Override
    public Component getName() {
        return Component.text("Revenant Rage", TextColor.color(0x5bc764), TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "first";
    }

    @Override
    public int getCooldown() {
        return 180;
    }

    @Override
    public Action getAction() {
        return Action.FIRST;
    }

    @Override
    public void activate(Player player) {
        if (player.getHealth() > 10.0) {
            return;
        }
        setActive(true);
        activePlayer.add(player.getUniqueId());

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > 20 * 5) {
                    cancel();
                    activePlayer.remove(player.getUniqueId());
                    setActive(false);
                    return;
                }

                ticks += 20;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!activePlayer.contains(player.getUniqueId())) return;

        event.setDamage(event.getDamage() * 1.25);
    }
}
