package net.kevarion.soulSMP.classes.jackolantern;

import com.mongodb.client.ListIndexesIterable;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class CreepingVines extends Ability {
    private static final int RADIUS = 8;
    private static final int STUN_DURATION = 4 * 20; //4s
    private static final int POISON_DURATION = 5 * 20;

    @Override
    public Component getName() {
        return Component.text("Creeping Vines", NamedTextColor.GOLD, TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "second";
    }

    @Override
    public int getCooldown() {
        return 180;
    }

    @Override
    public Action getAction() {
        return Action.SECOND;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        Location center = player.getLocation();
        World world = player.getWorld();

        for (Entity entity : world.getNearbyEntities(center, RADIUS, RADIUS, RADIUS)) {
            if (entity instanceof Player trapped && entity != player) {
                trapEntity(trapped);
            }
        }

        spawnVineAnimation(center);
        world.playSound(center, Sound.BLOCK_GRASS_BREAK, 2.0F, 0.5F);
    }

    private void trapEntity(Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, STUN_DURATION, 10, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, STUN_DURATION, 200, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, POISON_DURATION, 0, false, false));

        new BukkitRunnable() {
            int timer = STUN_DURATION / 2;

            @Override
            public void run() {
                if (timer <= 0 || !target.isOnline()) {
                    cancel();
                    setActive(false);
                    return;
                }
                target.setVelocity(new Vector(0, 0, 0));
                target.teleport(target.getLocation().setDirection(new Vector(0, 0, 1)));
                timer--;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 2L);
    }

    private void spawnVineAnimation(Location center) {
        World world = center.getWorld();
        new BukkitRunnable() {
            int height = 0;

            @Override
            public void run() {
                if (height >= 5) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 4) {
                    double x = RADIUS * Math.cos(angle);
                    double z = RADIUS * Math.sin(angle);
                    Location loc = center.clone().add(x, height, z);

                    world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 5, 0.1, 0.1, 0.1, 0.05);
                    world.spawnParticle(Particle.WITCH, loc,2, 0.1, 0.1, 0.1, 0.02);
                }

                world.playSound(center, Sound.BLOCK_VINE_STEP, 1.0F, 1.2F);
                height++;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 5L);
    }
}
