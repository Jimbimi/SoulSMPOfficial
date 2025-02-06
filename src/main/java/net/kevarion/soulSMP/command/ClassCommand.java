package net.kevarion.soulSMP.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.ClassManager;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.SMPClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.platform.Platform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("class")
@CommandPermission("soulsmp.*")
public class ClassCommand extends BaseCommand {

    ClassManager classManager = SoulSMP.getClassManager();

    @Default
    public void main(Player player) {
        player.sendMessage(Component.text("Provide args."));
    }

    @Subcommand("give")
    @CommandCompletion("@players @clist @empty")
    public void give(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Provide args").color(NamedTextColor.RED));
            return;
        }

        String playerName = args[0];
        String classIdentifier = args[1];

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            player.sendMessage(Component.text("Player not found!"));
            return;
        }

        SMPClass smpClass = classManager.getClassByIdentifier(classIdentifier);
        if (smpClass == null) {
            player.sendMessage(Component.text("Class not found!"));
            return;
        }

        classManager.giveClassToPlayer(player, classIdentifier);
        player.sendMessage(Component.text("You've given " + targetPlayer.getName() + " the class!", NamedTextColor.GREEN));
    }

    @Subcommand("remove")
    public void remove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Provide args").color(NamedTextColor.RED));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        String classIdentifier = args[1];

        if (targetPlayer == null) {
            player.sendMessage(Component.text("Player not found!"));
            return;
        }

        SMPClass smpClass = classManager.getClassByIdentifier(classIdentifier);
        if (smpClass == null) {
            player.sendMessage(Component.text("Class not found!"));
            return;
        }

        classManager.revokeClassFromPlayer(player, classIdentifier);
        player.sendMessage(Component.text("You've removed the class from " + targetPlayer.getName(), NamedTextColor.GREEN));
    }
}
