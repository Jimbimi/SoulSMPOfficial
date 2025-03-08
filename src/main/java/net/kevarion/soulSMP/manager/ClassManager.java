package net.kevarion.soulSMP.manager;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.classes.jackolantern.JackOLantern;
import net.kevarion.soulSMP.classes.reaper.Reaper;
import net.kevarion.soulSMP.classes.wisp.Wisp;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import javax.naming.Name;
import java.util.*;

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
        registerClass(new Wisp());
        registerClass(new JackOLantern());
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

    public void loadPlayerModules() {
        ConfigurationSection playersSection = SoulSMP.getDataManager().getPlayerDataConfig().getConfigurationSection("players");
        if (playersSection == null) {
            SoulSMP.getInstance().getLogger().warning("No players section found in player data configuration.");
            return;
        }

        for (String playerId : playersSection.getKeys(false)) {
            String moduleIdentifier = SoulSMP.getDataManager().getPlayerDataConfig().getString("players." + playerId + ".module");
            SMPClass smpClass = getClassByIdentifier(moduleIdentifier);
            if (smpClass != null) {
                registeredClasses.put(smpClass.getIdentifier(), smpClass);
            }
        }
    }

    public SMPClass getClassByIdentifier(String identifier) {
        return registeredClasses.get(identifier);
    }

    public boolean hasSelectedClass(Player player, String classIdentifier) {
        String selectedClassID = SoulSMP.getDataManager().getPlayerDataConfig().getString("players." + player.getUniqueId() + ".class");
        return selectedClassID != null && selectedClassID.equalsIgnoreCase(classIdentifier);
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
            player.sendMessage(Component.text("You must have a selected class to do this!", NamedTextColor.RED));
            return;
        }

        Ability ability = getClassAbilities(selectedClass.getIdentifier()).get(action);
        if (ability == null) {
            player.sendMessage(Component.text("Ability not found! Contact developers if this is an issue!", NamedTextColor.RED));
            return;
        }

        if (!ability.isUnlocked(player)) {
            player.sendMessage(Component.text("This ability is locked! Obtain more soul fragments to unlock it!").color(NamedTextColor.RED));
            return;
        }

        String displayName = LegacyComponentSerializer.legacySection().serialize(ability.getName());

        if (!cooldownManager.isOnCooldown(player, ability)) {
            ability.activate(player);
            cooldownManager.startCooldown(player, ability);
            player.sendMessage(Component.text("You have activated " + displayName, NamedTextColor.GREEN));
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
            player.sendMessage(Component.text("You've been given " + smpClass.getName(), NamedTextColor.GREEN));

            cooldownManager.showActionbar(player, smpClass);
        } else {
            player.sendMessage(Component.text("Class not found!").color(NamedTextColor.RED));
        }
    }

    public void revokeClassFromPlayer(Player player, String classIdentifier) {
        SMPClass smpClass = getClassByIdentifier(classIdentifier);
        if (smpClass != null) {
            player.getInventory().removeItem(smpClass.getItem());
            SoulSMP.getDataManager().getPlayerDataConfig().set("players." + player.getUniqueId() + ".class", null);
            SoulSMP.getDataManager().savePlayerDataConfig();
            player.sendMessage(Component.text("You've been removed from " + smpClass.getName()).color(NamedTextColor.RED));
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

    public ItemStack getSoulRerollerItem() {
        ItemStack item = new ItemStack(Material.WHITE_DYE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Soul Reroller", NamedTextColor.GREEN, TextDecoration.UNDERLINED));
        meta.lore(Arrays.asList(Component.text("Right click to get your class!", NamedTextColor.GRAY)));
        meta.setCustomModelData(100);
        item.setItemMeta(meta);

        return item;
    }

    public void startSoulReroller(Player player) {
        List<SMPClass> classList = new ArrayList<>(registeredClasses.values());

        if (classList.isEmpty()) {
            player.sendMessage(Component.text("There are no available classes!", NamedTextColor.RED));
            return;
        }

        Collections.shuffle(classList);
        final int[] taskId = new int[1];

        taskId[0] = Bukkit.getScheduler().runTaskTimer(SoulSMP.getInstance(), new Runnable() {
            private int counter = 0;
            private final Random random = new Random();
            private SMPClass selectedClass;
            String displayName = LegacyComponentSerializer.legacySection().serialize(selectedClass.getName());

            @Override
            public void run() {
                if (counter < 10) {
                    SMPClass randomClass = classList.get(random.nextInt(classList.size()));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);

                    player.showTitle(Title.title(
                            Component.text("ʀᴀɴᴅᴏᴍɪᴢɪɴɢ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text(displayName)
                    ));
                    counter++;
                } else {
                    Bukkit.getScheduler().cancelTask(taskId[0]);

                    selectedClass = classList.get(random.nextInt(classList.size()));
                    giveClassToPlayer(player, selectedClass.getIdentifier());

                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                    player.showTitle(Title.title(
                            Component.text("ꜱᴘɪʀɪᴛ ꜱᴇʟᴇᴄᴛᴇᴅ", NamedTextColor.GREEN),
                            Component.text(displayName)
                    ));
                    player.sendMessage(Component.text("You've been assigned: " + selectedClass.getName(), NamedTextColor.GREEN));
                }
            }
        }, 0L, 5L).getTaskId();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        if (mainHand != null && !mainHand.getType().isAir() && event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            long lastClickTime = player.hasMetadata("lastRerollerClick")
                    ? player.getMetadata("lastRerollerClick").get(0).asLong()
                    : 0;

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastClickTime < 250) {
                return;
            }
            player.setMetadata("lastRerollerClick", new FixedMetadataValue(SoulSMP.getInstance(), currentTime));

            if (mainHand == getSoulRerollerItem()) {
                startSoulReroller(player);
            }
        }
    }
}
