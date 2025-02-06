package net.kevarion.soulSMP.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.viaversion.viaversion.util.ChatColorUtil;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.SoulPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("trust")
public class TrustCommand extends BaseCommand {

    @Default
    public void main(Player player) {
        StringBuilder builder = new StringBuilder();

        SoulPlayer soulPlayer = PlayerManager.findSoulPlayer(player);
        if (soulPlayer == null) return;

        if (soulPlayer.getTrustedPlayerNames().isEmpty()) {
            player.sendMessage(Component.text("You don't have any trusted players!").color(NamedTextColor.RED));
            return;
        }

        builder.append(NamedTextColor.GREEN).append("Trusted Players: \n");
        soulPlayer.getTrustedPlayerNames().forEach(name -> builder.append(name).append("\n"));

        player.sendMessage(builder.toString());
    }

    @Subcommand("add")
    public void add(Player player, String[] args) {
        Player target = null;
        if ((target = Bukkit.getPlayer(args[0])) == null) {
            player.sendMessage(Component.text("Player not found!"));
            return;
        }

        if (target == player) {
            player.sendMessage(Component.text("You can't trust yourself!"));
            return;
        }

        SoulPlayer soulPlayer = PlayerManager.findSoulPlayer(player);
        if (soulPlayer == null) return;

        if (soulPlayer.getTrustedPlayerNames().contains(target.getName())) {
            player.sendMessage(Component.text("This player is already trusted!").color(NamedTextColor.RED));
            return;
        }

        soulPlayer.trust(target.getUniqueId().toString(), true);
        player.sendMessage(Component.text("You have trusted " + target.getName()).color(NamedTextColor.GREEN));
    }

    @Subcommand("remove")
    public void remove(Player player, String[] args) {
        Player target = null;
        if ((target = Bukkit.getPlayer(args[0])) == null) {
            player.sendMessage(Component.text("Player not found!"));
            return;
        }

        if (target == player) {
            player.sendMessage(Component.text("You can't un-trust yourself!"));
            return;
        }

        SoulPlayer soulPlayer = PlayerManager.findSoulPlayer(player);
        if (soulPlayer == null) return;

        if (!soulPlayer.getTrustedPlayerNames().contains(target.getName())) {
            player.sendMessage(Component.text("You can't un-trust a player that isn't trusted!").color(NamedTextColor.RED));
            return;
        }

        soulPlayer.removeTrust(target.getUniqueId().toString(), true);
        player.sendMessage(Component.text("You have un-trusted " + target.getName()).color(NamedTextColor.RED));
    }
}
