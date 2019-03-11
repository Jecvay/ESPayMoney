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
import org.spongepowered.api.text.Text;

public class ESPCommandManager {

    private ESPayMoney esp;
    private Logger logger;
    private final CommandCallable reloadCommand;

    public ESPCommandManager(ESPayMoney esp) {
        this.esp = esp;
        this.logger = esp.getLogger();

        this.reloadCommand = CommandSpec.builder()
                .description(I18N.getText("a"))
                .arguments(GenericArguments.none())
                .executor(this::processReloadCommand)
                .build();
    }

    private CommandResult processReloadCommand(CommandSource source, CommandContext args) throws CommandException {
        if (!source.hasPermission("espaymoney.reload")) {
            Text error = I18N.getText("cmd.no_permission", "reload");
            throw new CommandException(error);
        }
        esp.reloadPlugin();
        return CommandResult.success();
    }

    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .child(this.reloadCommand, "reload", "r")
                .build();
    }
}
