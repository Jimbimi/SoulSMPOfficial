package net.kevarion.soulSMP.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.kevarion.soulSMP.SoulSMP;
import net.kevarion.soulSMP.manager.ClassManager;
import net.kevarion.soulSMP.manager.PlayerManager;
import net.kevarion.soulSMP.manager.component.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("ability")
public class AbilityCommand extends BaseCommand {

    ClassManager classManager = SoulSMP.getClassManager();

    @Default
    public void main(Player player) {
        player.sendMessage(Component.text("Please provide arguments!").color(NamedTextColor.RED));
    }

    @Subcommand("first")
    public void first(Player player) {
        classManager.activateAbility(player, Ability.Action.FIRST);
    }

    @Subcommand("second")
    public void second(Player player) {
        classManager.activateAbility(player, Ability.Action.SECOND);
    }

    @Subcommand("third")
    public void third(Player player) {
        classManager.activateAbility(player, Ability.Action.THIRD);
    }
}
