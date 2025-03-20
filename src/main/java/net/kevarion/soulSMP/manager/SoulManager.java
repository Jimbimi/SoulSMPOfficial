package net.kevarion.soulSMP.manager;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kevarion.soulSMP.storage.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SoulManager implements Listener {
    private final SoulSMP instance;
    private DataManager dataManager = SoulSMP.getDataManager();
    private ClassManager classManager = SoulSMP.getClassManager();

    public SoulManager(SoulSMP instance) {
        this.instance = instance;
        startSoulCheckTask();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (dataManager.getPlayerDataConfig().get("players." + player.getUniqueId()) == null) {
            setSoulFragment(player, 1);
            player.getInventory().addItem(new ClassManager(SoulSMP.getCooldownManager()).getSoulRerollerItem());
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player killer = event.getEntity().getKiller();

            if (killer != null) {
                addSoulFragments(killer, 1);
                killer.sendMessage(Component.text("You gained +1 soul fragment!", NamedTextColor.GREEN));

                removeSoulFragment(player, 1);
                player.sendMessage(Component.text("You lost -1 soul fragment!", NamedTextColor.RED));
            }
        }
    }

    public void setSoulFragment(Player player, int amount) {
        dataManager.getPlayerDataConfig().set("players." + player.getUniqueId() + ".soul-fragments", amount);
        dataManager.savePlayerDataConfig();
        unlockAbilities(player, amount);
    }

    public void addSoulFragments(Player player, int amount) {
        int currentFragments = SoulSMP.getDataManager().getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".soul-fragments", 0);
        int newFragmentCount = Math.max(0, currentFragments + amount);

        SoulSMP.getDataManager().getPlayerDataConfig().set("players." + player.getUniqueId() + ".soul-fragments", newFragmentCount);
        SoulSMP.getDataManager().savePlayerDataConfig();

        System.out.println("Added " + amount + " soul fragments to " + player.getName() + ". Total: " + newFragmentCount);
        unlockAbilities(player, newFragmentCount);
    }

    public void removeSoulFragment(Player player, int amount) {
        int currentFragments = getSoulFragments(player);
        int newAmount = Math.max(0, currentFragments - amount);
        dataManager.getPlayerDataConfig().set("players." + player.getUniqueId() + ".soul-fragments", newAmount);
        dataManager.savePlayerDataConfig();
        unlockAbilities(player, newAmount);
    }

    public int getSoulFragments(Player player) {
        return dataManager.getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".soul-fragments");
    }

    private void unlockAbilities(Player player, int soulFragments) {
        SMPClass smpClass = SoulSMP.getClassManager().getSelectedClass(player);
        if (smpClass != null) {
            for (Ability ability : smpClass.getAbilities()) {
                boolean shouldUnlock = false;
                if (ability.getAction() == Ability.Action.FIRST && soulFragments >= 1) {
                    shouldUnlock = true;
                } else if (ability.getAction() == Ability.Action.SECOND && soulFragments >= 2) {
                    shouldUnlock = true;
                } else if (ability.getAction() == Ability.Action.THIRD && soulFragments >= 3) {
                    shouldUnlock = true;
                }
                ability.setUnlocked(player, shouldUnlock);
                //System.out.println("[DEBUG] For player " + player.getName() + ", ability "
                        //+ ability.getName() + " unlocked: " + shouldUnlock);
            }
        }
    }

    private void startSoulCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int soulFragments = getSoulFragments(player);
                    unlockAbilities(player, soulFragments);
                }
            }
        }.runTaskTimer(instance, 0L, 20L);
    }
}
