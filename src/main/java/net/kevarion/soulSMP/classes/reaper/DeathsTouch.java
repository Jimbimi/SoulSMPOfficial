package net.kevarion.soulSMP.classes.reaper;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathsTouch extends Ability {
    @Override
    public Component getName() {
        return Component.text("Death's Touch", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "second";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public Action getAction() {
        return Action.SECOND;
    }

    @Override
    public void activate(Player player) {
        setActive(true);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 40) {
                    cancel();
                    setActive(false);

                    player.removePotionEffect(PotionEffectType.STRENGTH);
                    return;
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 10 * 20, 1));

                double radius = 3;
                for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 8) {
                    for (double phi = 0; phi < Math.PI; phi += Math.PI / 8) {
                        double x = radius * Math.sin(phi) * Math.cos(theta);
                        double y = radius * Math.cos(phi);
                        double z = radius * Math.sin(phi) * Math.sin(theta);

                        player.getWorld().spawnParticle(Particle.DUST,
                            player.getLocation().add(x, y + 1, z), 1,
                            new Particle.DustOptions(Color.RED, 1)
                        );
                    }
                }
                ticks++;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0, 1);
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
