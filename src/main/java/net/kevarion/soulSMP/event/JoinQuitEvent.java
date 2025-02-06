package net.kevarion.soulSMP.event;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.ClassManager;
import net.kevarion.soulSMP.manager.component.SMPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClassManager classManager = SoulSMP.getClassManager();

        SMPClass playerClass = classManager.getSelectedClass(player);

        if (playerClass != null) {
            SoulSMP.getCooldownManager().cancelActionBarTask(player);
            SoulSMP.getCooldownManager().showActionbar(player, playerClass);
        }
    }
}
