package com.epam.warehouse.controller.command;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;

/**
 * One thing the user can ask for (Command pattern): each command is its own class.
 */
@FunctionalInterface
public interface Command {

    Response execute(Request request);
}
