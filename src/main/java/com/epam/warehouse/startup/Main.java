package com.epam.warehouse.startup;

import com.epam.warehouse.view.View;
import com.epam.warehouse.view.ViewFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Starts the application: reads the options and settings, builds the layers and runs the view.
 */
public final class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final String LOGGING_CONFIG = "logging.properties";
    private static final String CANT_START = "The application can't start: ";
    private static final int FAILURE = 1;

    private Main() {
    }

    public static void main(String[] args) {
        configureLogging();
        try {
            new PropertiesConfigImpl(LaunchOptions.parse(args)).init();
        } catch (RuntimeException e) {
            // no view exists yet, so the console is the only way to tell the user
            LOG.log(Level.SEVERE, "Startup failed", e);
            System.err.println(CANT_START + e.getMessage());
            System.exit(FAILURE);
        }
        View view = ViewFactory.getInstance();
        try {
            view.start();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "The application crashed", e);
            view.crash();
            System.exit(FAILURE);
        }
    }

    /**
     * Sends log records to warehouse.log instead of the console, where they would mix with the
     * answers.
     */
    private static void configureLogging() {
        try (InputStream config = Main.class.getClassLoader().getResourceAsStream(LOGGING_CONFIG)) {
            if (config != null) {
                LogManager.getLogManager().readConfiguration(config);
            }
        } catch (IOException e) {
            System.err.println("Logging is off: " + e.getMessage());
        }
    }
}
