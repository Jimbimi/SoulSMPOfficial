package net.kevarion.soulSMP.manager;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kevarion.soulSMP.storage.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SoulManager implements Listener {
    private final SoulSMP instance;
    private DataManager dataManager = SoulSMP.getDataManager();
    private ClassManager classManager = SoulSMP.getClassManager();

    public SoulManager(SoulSMP instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            setSoulFragment(player, 0);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player killer = event.getEntity().getKiller();

            if (killer != null) {
                giveSoulFragment(killer, 1);
            }
            if (getSoulFragments(player) <= 0) {
                removeSoulFragment(player, 1);
            }
        }
    }

    public void setSoulFragment(Player player, int amount) {
        dataManager.getPlayerDataConfig().set("players." + player.getUniqueId() + ".soul-fragments", amount);
        dataManager.savePlayerDataConfig();
    }

    public void giveSoulFragment(Player player, int amount) {
        dataManager.getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".soul-fragments", getSoulFragments(player) + amount);
        dataManager.savePlayerDataConfig();
    }

    public void removeSoulFragment(Player player, int amount) {
        if (getSoulFragments(player) == 0) return;
        dataManager.getPlayerDataConfig().set("players." + player.getUniqueId() + ".soul-fragments", getSoulFragments(player) - amount);
        dataManager.savePlayerDataConfig();
    }

    public int getSoulFragments(Player player) {
        return dataManager.getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".soul-fragments");
    }
}
