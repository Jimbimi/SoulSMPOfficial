package net.kevarion.soulSMP.manager;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.classes.reaper.Reaper;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassManager implements Listener {

    private final Map<String, SMPClass> registeredClasses = new HashMap<>();
    private final CooldownManager cooldownManager;

    public ClassManager(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
        registerAllAvailableClasses();
        Bukkit.getPluginManager().registerEvents(this, SoulSMP.getInstance());
    }

    public void registerAllAvailableClasses() {
        registerClass(new Reaper());
    }

    public void registerClass(SMPClass smpClass) {
        if (registeredClasses.containsKey(smpClass.getIdentifier())) {
            SoulSMP.getInstance().getLogger().warning("SMPClass " + smpClass.getIdentifier() + " is already registered!");
            return;
        }
        registeredClasses.put(smpClass.getIdentifier(), smpClass);
        SoulSMP.getDataManager().getPlayerDataConfig().set("classes." + smpClass.getIdentifier(), smpClass.getIdentifier());
        SoulSMP.getDataManager().savePlayerDataConfig();
    }

    public Map<String, SMPClass> getRegisteredClasses() {
        return registeredClasses;
    }

    public SMPClass getClassByIdentifier(String identifier) {
        return registeredClasses.get(identifier);
    }

    public boolean hasSelectedClass(Player player, String classIdentifier) {
        String selectedClassID = SoulSMP.getDataManager().getPlayerDataConfig().getString("players." + player.getUniqueId() + ".class");
        return selectedClassID != null && selectedClassID.equalsIgnoreCase(selectedClassID);
    }

    public Map<Ability.Action, Ability> getClassAbilities(String classIdentifier) {
        SMPClass smpClass = getClassByIdentifier(classIdentifier);
        if (smpClass == null) return null;

        Map<Ability.Action, Ability> abilitiesMap = new HashMap<>();
        for (Ability ability : smpClass.getAbilities()) {
            abilitiesMap.put(ability.getAction(), ability);
        }
        return abilitiesMap;
    }

    public Collection<SMPClass> getAllClasses() {
        return registeredClasses.values();
    }

    public void activateAbility(Player player, Ability.Action action) {
        SMPClass selectedClass = getSelectedClass(player);

        if (selectedClass == null) {
            player.sendMessage(Component.text("You must have a selected class to do this!"));
            return;
        }

        Ability ability = getClassAbilities(selectedClass.getIdentifier()).get(action);
        if (ability == null) {
            player.sendMessage(Component.text("Ability not found! Contact developers if this is an issue!"));
            return;
        }

        if (!cooldownManager.isOnCooldown(player, ability)) {
            if (ability.isUnlocked()) {
                ability.activate(player);
                cooldownManager.startCooldown(player, ability);
                player.sendMessage(Component.text("You have activated " + ability.getName()).color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("This ability is locked! Obtain more soul fragments to unlock it!").color(NamedTextColor.RED));
                return;
            }
        } else {
            cooldownManager.showActionbar(player, selectedClass);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }
    }

    public void giveClassToPlayer(Player player, String classIdentifier) {
        SMPClass smpClass = getClassByIdentifier(classIdentifier);
        if (smpClass != null) {
            player.getInventory().addItem(smpClass.getItem());
            SoulSMP.getDataManager().getPlayerDataConfig().set("players." + player.getUniqueId() + ".class", classIdentifier);
            SoulSMP.getDataManager().savePlayerDataConfig();
            player.sendMessage(Component.text("You've been given " + smpClass.getName()).color(NamedTextColor.GREEN));

            cooldownManager.showActionbar(player, smpClass);
        } else {
            player.sendMessage(Component.text("Class not found!").color(NamedTextColor.RED));
        }
    }

    public void revokeClassFromPlayer(Player player, String classIdentifier) {
        SMPClass smpClass = getClassByIdentifier(classIdentifier);
        if (smpClass != null) {
            player.getInventory().removeItem(smpClass.getItem());
            SoulSMP.getDataManager().getPlayerDataConfig().set("players." + player.getUniqueId() + ".smpClass", classIdentifier);
            SoulSMP.getDataManager().savePlayerDataConfig();
            player.sendMessage(Component.text("You've been removed of " + smpClass.getName()).color(NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("Class not found!").color(NamedTextColor.RED));
        }
    }

    public SMPClass getSelectedClass(Player player) {
        String classID = SoulSMP.getDataManager().getPlayerDataConfig().getString("players." + player.getUniqueId() + ".class");

        if (classID != null) {
            return getClassByIdentifier(classID);
        }
        return null;
    }
}
