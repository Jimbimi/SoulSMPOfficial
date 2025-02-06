package net.kevarion.soulSMP.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.CooldownManager;
import net.kevarion.soulSMP.manager.SoulManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("soulsmp|ssmp")
@CommandPermission("soulsmp.*")
public class MainCommand extends BaseCommand {

    private SoulManager soulManager = SoulSMP.getSoulManager();

    @Default
    public void main(Player player) {
        player.sendMessage(Component.text("Please provide arguments!").color(NamedTextColor.RED));
    }

    @Subcommand("givesoul")
    public void soulFragmentGive(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /soulsmp givesoulfragment [player] [amount]"));
        }

        String playerName = args[0];
        int amount = Integer.parseInt(args[1]);

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage(Component.text("Player not found or doesn't exist!"));
            return;
        }

        soulManager.addSoulFragments(target, amount);
        if (amount >= 2) {
            player.sendMessage(Component.text("You gave " + target.getName() + " " + amount + " soul fragments!").color(NamedTextColor.GREEN));
        } else if (amount == 1) {
            player.sendMessage(Component.text("You gave " + target.getName() + " " + amount + " soul fragment!").color(NamedTextColor.GREEN));
        }
    }

    @Subcommand("removesoul")
    public void soulFragmentRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /soulsmp removesoulfragment [player] [amount]"));
        }

        String playerName = args[0];
        int amount = Integer.parseInt(args[1]);

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage(Component.text("Player not found or doesn't exist!"));
            return;
        }

        soulManager.removeSoulFragment(target, amount);
        if (amount >= 2) {
            player.sendMessage(Component.text("You have removed " + amount + " soul fragments from " + target.getPlayer()).color(NamedTextColor.GREEN));
        } else if (amount == 1) {
            player.sendMessage(Component.text("You have removed " + amount + " soul fragment from " + target.getPlayer()).color(NamedTextColor.GREEN));
        }
    }

    @Subcommand("resetcooldowns")
    @CommandCompletion("all")
    public void reset(Player player, String[] args) {
        CooldownManager cooldownManager = SoulSMP.getCooldownManager();

        if (args.length == 0) {
            cooldownManager.resetPlayerCooldown(player);
        }

        if (args[0].equalsIgnoreCase("all")) {
            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                cooldownManager.resetPlayerCooldown(allPlayers);
            }
        }
    }

}
