package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;

/**
 * {@code exit}: tells the view to stop.
 */
public final class ExitCommand implements Command {

    private static final String GOODBYE = "Goodbye!";

    @Override
    public Response execute(Request request) {
        return Response.exit(GOODBYE);
    }
}
