package com.jecvay.ecosuites.espaymoney.Manager;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import com.jecvay.ecosuites.espaymoney.I18N;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ESPCommandManager {

    private ESPayMoney esp;
    private Logger logger;
    private final CommandCallable reloadCommand;
    private final CommandCallable debugCommand;
    private final CommandCallable fastEditCommand;

    public ESPCommandManager(ESPayMoney esp) {
        this.esp = esp;
        this.logger = esp.getLogger();

        this.reloadCommand = CommandSpec.builder()
                .description(I18N.getText("a"))
                .arguments(GenericArguments.none())
                .executor(this::processReloadCommand)
                .build();
        this.debugCommand = CommandSpec.builder()
                .description(I18N.getText("b"))
                .arguments(GenericArguments.none())
                .executor(this::processDebugCommand)
                .build();
        this.fastEditCommand = CommandSpec.builder()
                .description(I18N.getText("c"))
                .arguments(GenericArguments.none())
                .executor(this::processFastEditCommand)
                .build();
    }

    private CommandResult processReloadCommand(CommandSource source, CommandContext args) throws CommandException {
        if (!source.hasPermission("espaymoney.admin.reload")) {
            Text error = I18N.getText("cmd.no_permission", "reload");
            throw new CommandException(error);
        }
        esp.reloadPlugin();
        return CommandResult.success();
    }
    private CommandResult processDebugCommand(CommandSource source, CommandContext args) throws CommandException {
        if (!source.hasPermission("espaymoney.admin.debug")) {
            Text error = I18N.getText("cmd.no_permission", "debug");
            throw new CommandException(error);
        }
        if (source instanceof Player) {
            Player player = (Player) source;
            boolean tag = !esp.getDebugMode().isDebug(player.getUniqueId());
            esp.getDebugMode().setDebug(player.getUniqueId(), tag);
            player.sendMessage(I18N.getText("cmd.debug.set_tag", tag));
        } else {
            source.sendMessage(I18N.getText("cmd.only_player"));
        }

        return CommandResult.success();
    }
    private CommandResult processFastEditCommand(CommandSource source, CommandContext args) throws CommandException {
        if (!source.hasPermission("espaymoney.admin.edit")) {
            Text error = I18N.getText("cmd.no_permission", "fastEdit");
            throw new CommandException(error);
        }
        if (source instanceof Player) {
            Player player = (Player) source;
            boolean tag = !esp.getDebugMode().isEdit(player.getUniqueId());
            esp.getDebugMode().setEdit(player.getUniqueId(), tag);
            player.sendMessage(I18N.getText("cmd.edit.set_tag", tag));
        } else {
            source.sendMessage(I18N.getText("cmd.only_player"));
        }
        return CommandResult.success();
    }

    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .child(this.reloadCommand, "reload", "r")
                .child(this.debugCommand, "debug")
                .child(this.fastEditCommand, "fast")
                .build();
    }
}
