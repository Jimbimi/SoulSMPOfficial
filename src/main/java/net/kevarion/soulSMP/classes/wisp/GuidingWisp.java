package net.kevarion.soulSMP.classes.wisp;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GuidingWisp extends Ability {

    @Override
    public Component getName() {
        return Component.text("Guiding Wisp", TextColor.color(0x7ed3ed), TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "guiding-wisp";
    }

    @Override
    public int getCooldown() {
        return 180;  // 3 minutes cooldown
    }

    @Override
    public Action getAction() {
        return Action.SECOND;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        Location location = player.getLocation();

        // Create a wisp that moves forward
        BukkitRunnable abilityTask = new BukkitRunnable() {
            int ticks = 0;
            Location wispLocation = location.clone().add(0, 1, 0);  // Start above the player
            final int maxDistance = 15;  // Maximum travel distance for the wisp

            @Override
            public void run() {
                if (ticks >= maxDistance || !player.isOnline()) {
                    cancel();
                    setActive(false);
                    return;
                }

                // Particle trail following the wisp
                player.getWorld().spawnParticle(Particle.GLOW, wispLocation, 1, 0.5, 0.5, 0.5, 0.05);  // Glowing particles

                // Move wisp forward
                wispLocation.add(wispLocation.getDirection().multiply(1));

                // Mark enemies in the path
                for (Entity entity : player.getWorld().getNearbyEntities(wispLocation, 3, 3, 3)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;

                        // Mark the enemy with glowing effect and slow them
                        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1));  // 5 seconds glow
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));  // 5 seconds slow

                        // If the wisp touches the enemy, it bursts
                        if (wispLocation.distance(target.getLocation()) < 1) {
                            wispLocation.getWorld().spawnParticle(Particle.EXPLOSION, wispLocation, 10);
                            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));  // Weakness for 5 seconds
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));  // Slowness for 3 seconds
                            target.damage(4);  // Minor damage (2 hearts)

                            // Remove wisp after collision
                            cancel();
                            setActive(false);
                            return;
                        }
                    }
                }

                ticks++;
            }
        };

        abilityTask.runTaskTimer(SoulSMP.getInstance(), 0, 1);
    }
}
