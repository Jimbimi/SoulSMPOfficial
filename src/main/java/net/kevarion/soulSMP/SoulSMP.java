package net.kevarion.soulSMP;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.kevarion.soulSMP.command.AbilityCommand;
import net.kevarion.soulSMP.command.ClassCommand;
import net.kevarion.soulSMP.command.MainCommand;
import net.kevarion.soulSMP.command.TrustCommand;
import net.kevarion.soulSMP.event.JoinQuitEvent;
import net.kevarion.soulSMP.manager.ClassManager;
import net.kevarion.soulSMP.manager.CooldownManager;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.SoulManager;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kevarion.soulSMP.manager.revive.ReviveManager;
import net.kevarion.soulSMP.storage.DataManager;
import net.kevarion.soulSMP.util.CC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
public final class SoulSMP extends JavaPlugin {

    @Getter private static SoulSMP instance;
    @Getter private static DataManager dataManager;
    @Getter private static SoulManager soulManager;
    @Getter private static PlayerManager playerManager;
    @Getter private static CooldownManager cooldownManager;
    @Getter private static ClassManager classManager;
    @Getter private static ReviveManager reviveManager;

    private PaperCommandManager commandManager;

    public static SoulSMP getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(Component.text("Soul SMP enabled!").color(NamedTextColor.GREEN));

        instance = this;
        dataManager = new DataManager(this);
        soulManager = new SoulManager(this);
        playerManager = new PlayerManager();
        cooldownManager = new CooldownManager(this);
        classManager = new ClassManager(cooldownManager);
        reviveManager = new ReviveManager(this);

        cooldownManager.setClassManager(classManager);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        dataManager.initializePlayerDataConfig();

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.kick(Component.text("SoulSMP enabling, rejoin!", NamedTextColor.GREEN));
        }
        playerManager.registerPlayers();

        registerCommands();
        registerEvents();
        registerRecipes();

        classManager.loadPlayerModules();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Component.text("Soul SMP disabled!").color(NamedTextColor.RED));
        dataManager.savePlayerDataConfig();

        for (SoulPlayer soulPlayer : PlayerManager.getSoulPlayers()) {
            if (soulPlayer.getPlayer() != null) {
                soulPlayer.savePlayerData();
            } else {
                getLogger().warning("SoulPlayer has a null player object. Skipping data save for this player.");
            }
        }
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new MainCommand());
        commandManager.registerCommand(new TrustCommand());
        commandManager.registerCommand(new ClassCommand());
        commandManager.registerCommand(new AbilityCommand());

        commandManager.getCommandCompletions().registerCompletion("clist", c -> {
            List<String> classIds = new ArrayList<>();
            for (SMPClass smpClass : SoulSMP.getClassManager().getRegisteredClasses().values()) {
                classIds.add(smpClass.getIdentifier());
            }
            return classIds;
        });
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(cooldownManager, this);
        pm.registerEvents(soulManager, this);
        pm.registerEvents(classManager, this);
        pm.registerEvents(reviveManager, this);

        pm.registerEvents(new JoinQuitEvent(), this);
    }

    private void registerRecipes() {
        // not yet!!
    }
}
