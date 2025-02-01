package net.kevarion.soulSMP.manager.component;

import lombok.Getter;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.storage.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class SoulPlayer {

    @Getter private final Player player;
    @Getter private SMPClass selectedSMPClass;
    @Getter private final List<SMPClass> availableSMPClasses = new ArrayList<>();

    @Getter private final OfflinePlayer offlinePlayer;
    @Getter private final UUID uuid;
    private final List<String> trustedPlayerUUIDs = new ArrayList<>();

    public SoulPlayer(OfflinePlayer offlinePlayer) {
        this.player =offlinePlayer.getPlayer();
        this.offlinePlayer = offlinePlayer;
        this.uuid = offlinePlayer.getUniqueId();
    }

    public void savePlayerData() {
        DataManager dataManager = SoulSMP.getDataManager();
        if (!trustedPlayerUUIDs.isEmpty()) dataManager.getPlayerDataConfig().set("players." + player.getUniqueId() + ".trusted", trustedPlayerUUIDs);
        dataManager.savePlayerDataConfig();
    }

    public void trust(String playerUUID, boolean save) {
        trustedPlayerUUIDs.add(playerUUID);
        if (save) savePlayerData();
    }

    public void removeTrust(String playerUUID, boolean save) {
        trustedPlayerUUIDs.remove(playerUUID);
        if (save) savePlayerData();
    }

    public Set<String> getTrustedPlayerNames() {
        Set<String> names = new HashSet<>();
        trustedPlayerUUIDs.forEach(uuid -> names.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
        return names;
    }

    public boolean isTrusted(LivingEntity entity) {
        if (!(entity instanceof Player p)) return false;
        return trustedPlayerUUIDs.contains(p.getUniqueId().toString());
    }
}
