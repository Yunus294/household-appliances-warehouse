package com.epam.warehouse.startup;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LaunchOptionsTest {

    @Test
    void defaultsToAppPropertiesAndTheConsole() {
        LaunchOptions options = LaunchOptions.parse();

        assertEquals("app", options.configName());
        assertEquals(Optional.empty(), options.batchFile());
    }

    @Test
    void readsBothOptions() {
        LaunchOptions options = LaunchOptions.parse("--config=single-file", "--batch=examples/demo.txt");

        assertEquals("single-file", options.configName());
        assertEquals(Optional.of(Path.of("examples/demo.txt")), options.batchFile());
    }

    @Test
    void bareWordIsTheConfigName() {
        assertEquals("test", LaunchOptions.parse("test").configName());
    }

    @Test
    void unknownOptionIsRejected() {
        ConfigException e = assertThrows(ConfigException.class, () -> LaunchOptions.parse("--verbose"));
        assertEquals("Unknown option '--verbose'. Usage: java -jar household-appliances-warehouse.jar"
                + " [--config=<name>] [--batch=<file>]", e.getMessage());
    }

    @Test
    void optionNeedsAValue() {
        assertThrows(ConfigException.class, () -> LaunchOptions.parse("--batch="));
    }
}
