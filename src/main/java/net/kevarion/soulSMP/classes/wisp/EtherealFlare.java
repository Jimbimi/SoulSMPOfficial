package net.kevarion.soulSMP.classes.wisp;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EtherealFlare extends Ability {
    @Override
    public Component getName() {
        return Component.text("Ethereal Flare", TextColor.color(0x7ed3ed), TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "third";
    }

    @Override
    public int getCooldown() {
        return 120; // 2 minutes cooldown
    }

    @Override
    public Action getAction() {
        return Action.THIRD;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        Location location = player.getLocation();

        // Shockwave effect and applying debuffs to enemies in range
        BukkitRunnable abilityTask = new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 140;

            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) {
                    cancel();
                    setActive(false);
                    return;
                }

                World world = player.getWorld();

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 7, 7, 7)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;

                        // Apply the disorienting effects
                        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 140, 0)); // 7 seconds (140 ticks)
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 140, 0)); // 7 seconds (140 ticks)
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 140, 0)); // 7 seconds (140 ticks)

                        // Play an explosive sound for impact
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
                    }
                }

                ticks += 20;
            }
        };

        abilityTask.runTaskTimer(SoulSMP.getInstance(), 0, 1);
    }
}
