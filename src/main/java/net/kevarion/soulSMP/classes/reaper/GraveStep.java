package net.kevarion.soulSMP.classes.reaper;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GraveStep extends Ability {
    private static final double SEARCH_RADIUS = 5.0;
    private static final double DAMAGE = 10.0;

    @Override
    public Component getName() {
        return Component.text("Grave Step", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "third";
    }

    @Override
    public int getCooldown() {
        return 60;
    }

    @Override
    public Action getAction() {
        return Action.THIRD;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        List<Player> nearbyPlayers = player.getNearbyEntities(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .filter(target -> !target.equals(player))
                .collect(Collectors.toList());

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(Component.text("There are no nearby entities to use this ability on!"));
            return;
        }

        Player target = nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
        playDarkMistEffect(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                Location targetLocation = target.getLocation();
                Location behindTarget = targetLocation.clone().add(targetLocation.getDirection().multiply(-1).setY(0));

                player.teleport(behindTarget);
                playShadowBurstEffect(behindTarget);
                target.damage(DAMAGE, player);
                playImpactEffect(target.getLocation());

                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.5F, 1.0F);
            }
        }.runTaskLater(SoulSMP.getInstance(), 10L);
    }

    private void playDarkMistEffect(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();
        new BukkitRunnable() {
            double radius = 0.6;
            double angle = 0;
            int loops = 20;

            @Override
            public void run() {
                if (loops <= 0) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 4; i++) {
                    double x=  radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    world.spawnParticle(Particle.SMOKE, loc.clone().add(x, 0.2, z), 1, 0, 0, 0, 0);
                    angle += Math.PI / 8;
                }

                loops--;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 2L);
    }

    private void playShadowBurstEffect(Location location) {
        World world = location.getWorld();
        for (int i = 0; i < 20; i++) {
            double angle = (Math.PI * 2 * i) / 20;
            double x = Math.cos(angle) * 1.5;
            double z = Math.sin(angle) * 1.5;
            world.spawnParticle(Particle.LARGE_SMOKE, location.clone().add(x, 0.2, z), 1, 0, 0, 0, 0);
        }
        world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.2F, 0.6F);
    }

    private void playImpactEffect(Location location) {
        World world = location.getWorld();

        for (int i = 0; i < 20; i++) {
            double angle = (Math.PI * 2 * i) / 20;
            double x = Math.cos(angle) * 2;
            double z = Math.sin(angle) * 2;
            world.spawnParticle(Particle.CLOUD, location.clone().add(x, 0.1, z), 1, 0, 0, 0, 0);
        }

        world.spawnParticle(Particle.BLOCK, location.clone().add(0, 1, 0), 20, 0.3, 0.3, 0.3, Material.REDSTONE_BLOCK.createBlockData());
        world.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.5F, 0.8F);
    }
}
