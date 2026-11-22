package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;

/**
 * Answers any command the application doesn't know.
 */
public final class WrongCommand implements Command {

    @Override
    public Response execute(Request request) {
        return Response.error("Unknown command '" + request.command() + "'. Type 'help' to see the commands");
    }
}
