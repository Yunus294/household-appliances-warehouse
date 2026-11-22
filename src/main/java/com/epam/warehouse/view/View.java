package com.epam.warehouse.view;

/**
 * Talks to the user: reads requests, passes them to the controller, shows the answers.
 */
public interface View {

    /**
     * Shows the greeting and the menu, then handles requests until the user quits or the input ends.
     */
    void start();

    /**
     * Tells the user that the application hit an unexpected error and has to stop.
     */
    void crash();
}
