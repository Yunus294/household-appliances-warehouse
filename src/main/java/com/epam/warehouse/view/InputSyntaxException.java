package com.epam.warehouse.view;

/**
 * The typed line can't be a request at all: an unclosed quote, a parameter without a value...
 */
public class InputSyntaxException extends Exception {

    public InputSyntaxException(String message) {
        super(message);
    }
}
