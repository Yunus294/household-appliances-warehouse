package com.epam.warehouse.startup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * A {@code .properties} file from the classpath, with clear errors for missing settings.
 */
final class Settings {

    private static final String EXTENSION = ".properties";

    private final String fileName;
    private final Properties properties;

    private Settings(String fileName, Properties properties) {
        this.fileName = fileName;
        this.properties = properties;
    }

    /**
     * @param name file name without ".properties"
     * @throws ConfigException if the file is missing or unreadable
     */
    static Settings load(String name) {
        String fileName = name + EXTENSION;
        try (InputStream in = Settings.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new ConfigException("Settings file " + fileName + " not found");
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return new Settings(fileName, properties);
        } catch (IOException e) {
            throw new ConfigException("Can't read " + fileName, e);
        }
    }

    /**
     * @throws ConfigException if the setting is missing or empty
     */
    String required(String key) {
        return optional(key).orElseThrow(() ->
                new ConfigException("Setting '" + key + "' is missing in " + fileName));
    }

    /**
     * @return the trimmed value, or empty if the setting is missing or blank
     */
    Optional<String> optional(String key) {
        return Optional.ofNullable(properties.getProperty(key)).map(String::strip).filter(value -> !value.isEmpty());
    }

    String fileName() {
        return fileName;
    }
}
