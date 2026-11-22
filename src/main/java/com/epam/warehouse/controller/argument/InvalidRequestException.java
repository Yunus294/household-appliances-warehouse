package com.epam.warehouse.controller.argument;

/**
 * The request is wrong: unknown target, unknown parameter, a value that isn't a number...
 * The message is written for the user.
 */
public class InvalidRequestException extends Exception {

    public InvalidRequestException(String message) {
        super(message);
    }
}
