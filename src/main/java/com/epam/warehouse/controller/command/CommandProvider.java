package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.argument.QueryParser;
import com.epam.warehouse.controller.format.ApplianceFormatter;
import com.epam.warehouse.service.ApplianceService;

import java.util.EnumMap;
import java.util.Map;

/**
 * Finds the command by its name; unknown names get {@link WrongCommand}.
 */
public final class CommandProvider {

    private final Map<CommandName, Command> commands = new EnumMap<>(CommandName.class);
    private final Command wrongCommand = new WrongCommand();

    public CommandProvider(ApplianceService service) {
        QueryParser parser = new QueryParser();
        ApplianceFormatter formatter = new ApplianceFormatter();
        commands.put(CommandName.FIND, new FindCommand(service, parser, formatter));
        commands.put(CommandName.LIST, new ListCommand(service, parser, formatter));
        commands.put(CommandName.COST, new CostCommand(service, parser, formatter));
        commands.put(CommandName.HELP, new HelpCommand());
        commands.put(CommandName.EXIT, new ExitCommand());
    }

    public Command get(String name) {
        return CommandName.byToken(name).map(commands::get).orElse(wrongCommand);
    }
}
