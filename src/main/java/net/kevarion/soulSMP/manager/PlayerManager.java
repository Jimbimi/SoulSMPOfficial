package net.kevarion.soulSMP.manager;

import lombok.Getter;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kevarion.soulSMP.storage.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.AbstractQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerManager implements Listener {

    @Getter
    private static final Set<SoulPlayer> soulPlayers = new HashSet<>();

    private final DataManager dataManager = SoulSMP.getDataManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        registerPlayer(player);
        SoulPlayer soulPlayer = findSoulPlayer(player);

        if (soulPlayer == null) soulPlayer = new SoulPlayer(player);
        soulPlayers.add(soulPlayer);
    }

    public static SoulPlayer findSoulPlayer(Player player) {
        return soulPlayers.stream().filter(SoulPlayer -> {
            if (SoulPlayer.getPlayer() == null) return false;
            return SoulPlayer.getPlayer().equals(player);
        }).findFirst().orElse(null);
    }

    public static SoulPlayer findSoulPlayer(UUID uuid) {
        return soulPlayers.stream().filter(SoulPlayer -> {
            if (SoulPlayer.getUuid() == null) return false;
            return SoulPlayer.getUuid().equals(uuid);
        }).findFirst().orElse(null);
    }

    public static SoulPlayer findSoulPlayer(OfflinePlayer offlinePlayer) {
        return soulPlayers.stream().filter(SoulPlayer -> {
            if (SoulPlayer.getOfflinePlayer() == null) return false;
            return SoulPlayer.getOfflinePlayer().equals(offlinePlayer);
        }).findFirst().orElse(null);
    }

    public void registerPlayer(Player player) {
        FileConfiguration playerData = dataManager.getPlayerDataConfig();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        createSoulPlayer(playerData, offlinePlayer);
    }

    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void createSoulPlayer(FileConfiguration playerData, OfflinePlayer offlinePlayer) {
        SoulPlayer soulPlayer = new SoulPlayer(offlinePlayer);
        playerData.getStringList(offlinePlayer.getUniqueId() + ".trusted").forEach(uuid -> soulPlayer.trust(uuid, false));
        soulPlayers.add(soulPlayer);
    }
}