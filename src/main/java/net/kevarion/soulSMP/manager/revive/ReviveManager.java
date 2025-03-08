package net.kevarion.soulSMP.manager.revive;

import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.ClassManager;
import net.kevarion.soulSMP.manager.SoulManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviveManager implements Listener {

    private final SoulSMP instance;
    private ClassManager classManager;
    private SoulManager soulManager;

    public ReviveManager(SoulSMP instance) {
        this.instance = instance;
        classManager = SoulSMP.getClassManager();
        soulManager = SoulSMP.getSoulManager();
        startChecker();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (soulManager.getSoulFragments(event.getPlayer()) == 1) {
            event.getPlayer().sendMessage(Component.text("You have 1 life remaining before you get death banned. Be careful!"));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == getGravebringer()) {
            if (event.getAction().isRightClick()) {
                openGravebringerMenu(player);
                player.sendMessage(Component.text("You have opened the Gravebringer menu!", NamedTextColor.GREEN));
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null || !event.getView().title().equals(Component.text("Gravebringer Menu", NamedTextColor.RED, TextDecoration.BOLD)));
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
        assert meta != null;
        String playerName = meta.getOwningPlayer() != null ? meta.getOwningPlayer().getName() : null;

        if (playerName == null) return;
        OfflinePlayer deadPlayer = Bukkit.getOfflinePlayer(playerName);

        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(deadPlayer.getName())) {
            Bukkit.getBanList(BanList.Type.NAME).pardon(deadPlayer.getName());
            soulManager.setSoulFragment(player, 1);

            Bukkit.broadcast(Component.text(deadPlayer.getName(), NamedTextColor.GREEN)
                    .append(Component.text(" has been ", NamedTextColor.WHITE)
                    .append(Component.text("revived", NamedTextColor.GREEN)
                    .append(Component.text(" by", NamedTextColor.WHITE)
                    .append(Component.text(player.getName(), NamedTextColor.GREEN))))));

            player.sendMessage(Component.text("You revived " + deadPlayer.getName(), NamedTextColor.GREEN));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (soulManager.getSoulFragments(player) == 0) {
            Bukkit.broadcast(Component.text(player.getName(), NamedTextColor.RED).append(Component.text(" has been death banned!", NamedTextColor.WHITE)));
            player.kick(Component.text("You were banned for running out of soul fragments!", NamedTextColor.RED));
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), ChatColor.RED + "Ran out of soul fragments!", null, "");
        }
    }

    private void startChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.isBanned()) continue;
                    if (soulManager.getSoulFragments(players) >0) continue;

                    Bukkit.broadcast(Component.text(players.getName(), NamedTextColor.RED).append(Component.text(" has been death banned!", NamedTextColor.WHITE)));
                    players.kick(Component.text("You were banned for running out of soul fragments!", NamedTextColor.RED));
                    Bukkit.getBanList(BanList.Type.NAME).addBan(players.getName(), ChatColor.RED + "Ran out of soul fragments!", null, "");
                }
            }
        }.runTaskTimer(SoulSMP.getInstance(), 0L, 20L);
    }

    private void openGravebringerMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, Component.text("Gravebringer Menu", NamedTextColor.RED, TextDecoration.BOLD));

        List<OfflinePlayer> deathBannedPlayers = new ArrayList<>();
        for (BanEntry banEntry : Bukkit.getBanList(BanList.Type.NAME).getEntries()) {
            if (banEntry.getReason() != null && banEntry.getReason().equals(ChatColor.RED + "Ran out of soul fragments!")) {
                OfflinePlayer bannedPlayer = Bukkit.getOfflinePlayer(banEntry.getTarget());
                deathBannedPlayers.add(bannedPlayer);
            }
        }

        for (int i = 0; i < deathBannedPlayers.size(); i++) {
            OfflinePlayer bannedPlayer = deathBannedPlayers.get(i);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            assert skullMeta != null;
            skullMeta.displayName(Component.text(bannedPlayer.getName(), NamedTextColor.RED));
            skullMeta.setOwningPlayer(bannedPlayer);
            skull.setItemMeta(skullMeta);
            inventory.setItem(i, skull);
        }

        player.openInventory(inventory);
    }

    public ItemStack getGravebringer() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Gravebringer", TextColor.color(0x6e322e)));
        meta.lore(Arrays.asList(Component.text("Allows you to revive death banned players.")));
        meta.setCustomModelData(150);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isBanned(Player player) {
        return Bukkit.getBanList(BanList.Type.NAME).isBanned(player.getName());
    }
}
