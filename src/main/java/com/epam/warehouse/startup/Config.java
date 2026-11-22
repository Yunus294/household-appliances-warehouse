package com.epam.warehouse.startup;

/**
 * Prepares the application before it starts: builds every layer and initializes the factories.
 */
public interface Config {

    /**
     * @throws ConfigException if the configuration is missing or wrong
     */
    void init();
}
