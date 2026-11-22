package com.epam.warehouse.controller;

import com.epam.warehouse.controller.command.Command;
import com.epam.warehouse.controller.command.CommandProvider;
import com.epam.warehouse.service.ApplianceService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Front controller: finds the command by its name and lets it handle the request.
 */
public final class ControllerImpl implements Controller {

    private static final Logger LOG = Logger.getLogger(ControllerImpl.class.getName());

    private final CommandProvider commands;

    public ControllerImpl(ApplianceService service) {
        this.commands = new CommandProvider(service);
    }

    @Override
    public Response execute(Request request) {
        if (request == null) {
            return Response.error("Empty request");
        }
        Command command = commands.get(request.command());
        try {
            return command.execute(request);
        } catch (RuntimeException e) {
            // a bug, not a user mistake: keep the application running and leave a trace
            LOG.log(Level.SEVERE, "Unexpected error on " + request, e);
            return Response.error("Unexpected error: " + e.getMessage());
        }
    }
}
