package net.kevarion.soulSMP;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.kevarion.soulSMP.command.AbilityCommand;
import net.kevarion.soulSMP.command.ClassCommand;
import net.kevarion.soulSMP.command.MainCommand;
import net.kevarion.soulSMP.command.TrustCommand;
import net.kevarion.soulSMP.manager.ClassManager;
import net.kevarion.soulSMP.manager.CooldownManager;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.SoulManager;
import net.kevarion.soulSMP.storage.DataManager;
import net.kevarion.soulSMP.util.CC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("LombokGetterMayBeUsed")
public final class SoulSMP extends JavaPlugin {

    @Getter private static SoulSMP instance;
    @Getter private static DataManager dataManager;
    @Getter private static SoulManager soulManager;
    @Getter private static PlayerManager playerManager;
    @Getter private static CooldownManager cooldownManager;
    @Getter private static ClassManager classManager;

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

        cooldownManager.setClassManager(classManager);

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        dataManager.initializePlayerDataConfig();

        registerCommands();
        registerEvents();
        registerRecipes();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Component.text("Soul SMP disabled!").color(NamedTextColor.RED));
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new MainCommand());
        commandManager.registerCommand(new TrustCommand());
        commandManager.registerCommand(new ClassCommand());
        commandManager.registerCommand(new AbilityCommand());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(cooldownManager, this);
        pm.registerEvents(soulManager, this);
        pm.registerEvents(classManager, this);
    }

    private void registerRecipes() {
        // not yet!!
    }
}
