package net.kevarion.soulSMP.classes.jackolantern;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class PumpkinBomb extends Ability {
    private static final double EXPLOSION_RADIUS = 4.0;
    private static final double DAMAGE = 8.0;
    private static final int FIRE_DURATION = 5;

    @Override
    public Component getName() {
        return Component.text("Pumpkin Bomb", NamedTextColor.GOLD, TextDecoration.UNDERLINED);
    }

    @Override
    public String getIdentifier() {
        return "first";
    }

    @Override
    public int getCooldown() {
        return 60;
    }

    @Override
    public Action getAction() {
        return Action.FIRST;
    }

    @Override
    public void activate(Player player) {
        setActive(true);
        Location spawnLocation = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().multiply(1.5);

        ArmorStand pumpkin = spawnPumpkin(player, spawnLocation);

        new BukkitRunnable() {
            int lifespan = 40;

            @Override
            public void run() {
                if (lifespan <= 0 || !pumpkin.isDead()) {
                    explode(pumpkin.getLocation(), player);
                    pumpkin.remove();
                    setActive(false);
                    cancel();
                    return;
                }

                Location newLocation = pumpkin.getLocation().add(direction);
                pumpkin.teleport(newLocation);

                playFireTrailEffect(newLocation);
                lifespan--;
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 2L);
    }

    private ArmorStand spawnPumpkin(Player player, Location location) {
        World world = player.getWorld();
        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
        return stand;
    }

    private void explode(Location location, Player caster) {
        World world = location.getWorld();

        world.spawnParticle(Particle.EXPLOSION, location, 1);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);

        for (int i = 0; i < 10; i++) {
            double angle = Math.random() * Math.PI * 2;
            double x = Math.cos(angle) * EXPLOSION_RADIUS;
            double z = Math.sin(angle) * EXPLOSION_RADIUS;
            world.spawnParticle(Particle.FLAME, location.clone().add(x, 1, z), 5, 0.1, 0.1, 0.1, 0.02);
        }

        List<Entity> entities = location.getWorld().getNearbyEntities(location, EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS)
                .stream()
                .filter(e -> e instanceof LivingEntity && e != caster)
                .toList();

        for (Entity entity : entities) {
            ((LivingEntity) entity).damage(DAMAGE, caster);
            entity.setFireTicks(FIRE_DURATION * 20);
        }
    }

    private void playFireTrailEffect(Location location) {
        location.getWorld().spawnParticle(Particle.FLAME, location, 3, 0.1, 0.1, 0.1, 0.02);
        location.getWorld().spawnParticle(Particle.SMOKE, location, 2, 0.1, 0.1, 0.1, 0.02);
    }
}
