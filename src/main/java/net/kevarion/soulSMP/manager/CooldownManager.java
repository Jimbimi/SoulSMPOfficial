package net.kevarion.soulSMP.manager;

import lombok.Setter;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager implements Listener {

    @Setter private ClassManager classManager;
    private final Map<UUID, Map<String, Long>> playerCooldowns = new HashMap<>();
    private final Map<UUID, BukkitTask> actionBarTasks = new HashMap<>();

    public CooldownManager(SoulSMP instance) {
        loadCooldowns();
        startGlobalActionBarChecker(instance);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BukkitTask existingTask = actionBarTasks.get(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel();
        }

        BukkitTask newTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (SoulSMP.getDataManager().getPlayerDataConfig().getString("players." + player.getUniqueId()) == null) {
                    player.sendActionBar(Component.text("No current class!").color(NamedTextColor.GRAY));
                }
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 20L);

        actionBarTasks.put(player.getUniqueId(), newTask);
    }

    public boolean isOnCooldown(Player player, Ability ability) {
        Map<String, Long> cooldowns = playerCooldowns.get(player.getUniqueId());
        if (cooldowns == null || !cooldowns.containsKey(ability.getName().toString())) return false;

        long cooldownEndTime = cooldowns.get(ability.getName().toString());
        return System.currentTimeMillis() < cooldownEndTime;
    }

    public int getRemainingCooldown(Player player, Ability ability) {
        Map<String, Long> cooldowns = playerCooldowns.get(player.getUniqueId());
        if (cooldowns == null || !cooldowns.containsKey(ability.getName().toString())) return 0;

        long cooldownEndTime = cooldowns.get(ability.getName().toString());
        long remainingTime = cooldownEndTime - System.currentTimeMillis();
        return (int) (remainingTime / 1000L);
    }

    public void resetPlayerCooldown(Player player) {
        playerCooldowns.remove(player.getUniqueId());
    }

    public void showActionbar(Player player, SMPClass smpClass) {
        Bukkit.getLogger().info("[DEBUG] showActionbar called for player: " + player.getName());

        BukkitTask existingTask = actionBarTasks.get(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel();
        }

        BukkitTask newTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (smpClass == null || !player.getInventory().contains(smpClass.getItem()) || SoulSMP.getDataManager().getPlayerDataConfig().getString("players." + player.getUniqueId() + ".class") == null) {
                    player.sendActionBar(Component.text("No current class!").color(NamedTextColor.GRAY));
                    return;
                }

                Component actionBarMessage = Component.empty();
                for (int i = 0; i < smpClass.getAbilities().size(); i++) {
                    Ability ability = smpClass.getAbilities().get(i);
                    int remainingCooldown = getRemainingCooldown(player, ability);
                    String cooldownDisplay;

                    if (ability.isActive()) {
                        cooldownDisplay = "⌛";
                    } else if (!ability.isUnlocked()) {
                        cooldownDisplay = "❌";
                    } else {
                        cooldownDisplay = remainingCooldown > 0 ? remainingCooldown + "s" : "✔";
                    }

                    actionBarMessage = actionBarMessage.append(ability.getName())
                            .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                            .append(Component.text(cooldownDisplay, NamedTextColor.WHITE))
                            .append(Component.text("]", NamedTextColor.DARK_GRAY));

                    if (i < smpClass.getAbilities().size() - 1) {
                        actionBarMessage = actionBarMessage.append(Component.text(" | "));
                    }
                }

                player.sendActionBar(actionBarMessage);
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 20L);

        actionBarTasks.put(player.getUniqueId(), newTask);
    }

    public void cancelActionBarTask(Player player) {
        BukkitTask task = actionBarTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void startGlobalActionBarChecker(SoulSMP instance) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("[DEBUG] Global action bar checker running...");

                if (classManager == null) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    SMPClass smpClass = classManager.getSelectedClass(player);
                    if (smpClass != null) {
                        showActionbar(player, smpClass);
                    }
                }
            }
        }.runTaskTimer(instance, 0L, 20L);
    }

    public void startCooldown(Player player, Ability ability) {
        playerCooldowns.putIfAbsent(player.getUniqueId(), new HashMap<>());
        long cooldownEndTime = System.currentTimeMillis() + (ability.getCooldown() * 1000L);
        playerCooldowns.get(player.getUniqueId()).put(ability.getName().toString(), cooldownEndTime);

        savePlayerCooldown(player.getUniqueId(), ability.getName().toString(), cooldownEndTime);
    }

    private void savePlayerCooldown(UUID playerUUID, String abilityName, long cooldownEndTime) {
        SoulSMP.getDataManager().getPlayerDataConfig().set("players." + playerUUID + ".cooldowns." + abilityName, cooldownEndTime);
        SoulSMP.getDataManager().savePlayerDataConfig();
    }

    public void loadCooldowns() {
        ConfigurationSection playersSection = SoulSMP.getDataManager().getPlayerDataConfig().getConfigurationSection("players");
        if (playersSection == null) return;

        for (String playerId : playersSection.getKeys(false)) {
            UUID playerUUID = UUID.fromString(playerId);
            ConfigurationSection cooldownSection = playersSection.getConfigurationSection(playerId + ".cooldowns");
            if (cooldownSection != null) {
                Map<String, Long> playerCooldownMap = new HashMap<>();
                for (String abilityNameStr : cooldownSection.getKeys(false)) {
                    long cooldown = cooldownSection.getLong(abilityNameStr);
                    playerCooldownMap.put(abilityNameStr, cooldown);
                }
                playerCooldowns.put(playerUUID, playerCooldownMap);
            }
        }
    }
}
