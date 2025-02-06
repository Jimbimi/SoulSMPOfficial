package net.kevarion.soulSMP.classes.wisp;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class WispSwarm extends Ability {

    @Override
    public Component getName() {
        return Component.text("Wisp Swarm", TextColor.color(0x7ed3ed), TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "first";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public Action getAction() {
        return Action.FIRST;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        World world = player.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 200;
            double angle = 0;

            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) {
                    cancel();
                    setActive(false);
                    return;
                }

                Location center = player.getLocation().add(0, 1.5, 0);

                for (int i = 0; i < 3; i++) { // 3 Wisps
                    double offsetX = Math.cos(angle + (i * Math.PI * 2 / 3)) * 2;
                    double offsetZ = Math.sin(angle + (i * Math.PI * 2 / 3)) * 2;
                    Location wispLocation = center.clone().add(offsetX, Math.sin(angle) * 0.5, offsetZ);

                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, wispLocation, 2, 0, 0, 0, 0.05);
                }

                angle += Math.PI / 10;

                List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).damage(4);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0, 2);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public void setUnlocked(Player player, boolean unlocked) {
        super.setUnlocked(player, unlocked);
    }

    @Override
    public boolean isUnlocked(Player player) {
        return super.isUnlocked(player);
    }
}
