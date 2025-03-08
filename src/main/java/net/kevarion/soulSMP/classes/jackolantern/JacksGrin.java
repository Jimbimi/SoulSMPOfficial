package net.kevarion.soulSMP.classes.jackolantern;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class JacksGrin extends Ability {
    @Override
    public Component getName() {
        return Component.text("Jack's Grin", NamedTextColor.GOLD, TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "third";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public Action getAction() {
        return Action.THIRD;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        World world = player.getWorld();
        Location center = player.getLocation().add(0, 1, 0);

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20;

            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) {
                    cancel();
                    setActive(false);
                    return;
                }

                world.spawnParticle(Particle.LARGE_SMOKE, center, 5, 0.5, 0.5, 0.5, 0);
                world.spawnParticle(Particle.WITCH, center, 5, 0.5, 0.5, 0.5, 0);

                ticks++;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0, 5);

        SoulPlayer soulPlayer = PlayerManager.findSoulPlayer(player);
        if (soulPlayer == null) return;

        List<Entity> nearbyEntities = player.getNearbyEntities(8, 8, 8);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && entity != player) {
               LivingEntity target = (LivingEntity) entity;
               if (soulPlayer.isTrusted(target)) continue;

               target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 0));
            }
        }
    }
}
