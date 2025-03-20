package net.kevarion.soulSMP.classes.reaper;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class LifeStealer extends Ability {
    @Override
    public Component getName() {
        return Component.text("Lifestealer", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED);
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
        Location location = player.getLocation();

        BukkitRunnable abilityTask = new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 60;
            Random random = new Random();

            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) {
                    this.cancel();
                    setActive(false);

                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    return;
                }

                Location center = player.getLocation().add(0, 2, 0);
                Location currentLocation = player.getLocation();
                World world = player.getWorld();

                for (double t = 0; t < Math.PI * 2; t += Math.PI / 8) {
                    double x = Math.sin(t) * 1.5;
                    double z = Math.cos(t) * 1.5;
                    double y = Math.sin(ticks * 0.2) * 0.5;
                    world.spawnParticle(Particle.DUST, center.clone().add(x, y, z),
                            1, new Particle.DustOptions(Color.RED, 1.5F));
                }

                SoulPlayer soulPlayer = PlayerManager.findSoulPlayer(player);
                if (soulPlayer == null) return;

                for (Entity entity : player.getWorld().getNearbyEntities(currentLocation, 10, 10, 10)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;
                        if (soulPlayer.isTrusted(target)) continue;

                        target.damage(4);
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));

                        createEnergyBeam(center, target.getLocation().add(0, 1, 0));
                    }
                }

                ticks++;
            }
        };

        abilityTask.runTaskTimer(SoulSMP.getInstance(), 0, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
    }

    private void createEnergyBeam(Location start, Location end) {
        World world = start.getWorld();
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        double step = 0.5;

        for (double i = 0; i < distance; i+= step) {
            Location point = start.clone().add(direction.clone().multiply(i));
            world.spawnParticle(Particle.DUST, point, 1,
                    new Particle.DustOptions(Color.RED, 1.2F));
        }
    }
}
