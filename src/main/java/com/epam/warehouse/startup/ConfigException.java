package com.epam.warehouse.startup;

/**
 * The application can't be configured: a missing setting, an unknown class, a bad option...
 */
public class ConfigException extends RuntimeException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
