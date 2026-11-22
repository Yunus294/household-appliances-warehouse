package com.epam.warehouse.startup;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Command-line options.
 * <pre>
 * java -jar household-appliances-warehouse.jar [--config=&lt;name&gt;] [--batch=&lt;file&gt;]
 * </pre>
 * {@code --config=single-file} reads {@code single-file.properties} instead of
 * {@code app.properties}; {@code --batch=commands.txt} runs the requests from the file instead of
 * reading the console. A bare word is taken as the config name.
 */
public final class LaunchOptions {

    private static final String DEFAULT_CONFIG = "app";

    private static final String CONFIG = "--config=";
    private static final String BATCH = "--batch=";
    private static final String OPTION_PREFIX = "--";
    private static final String USAGE = "Usage: java -jar household-appliances-warehouse.jar"
            + " [" + CONFIG + "<name>] [" + BATCH + "<file>]";

    private final String configName;
    private final Path batchFile;

    private LaunchOptions(String configName, Path batchFile) {
        this.configName = configName;
        this.batchFile = batchFile;
    }

    /**
     * @throws ConfigException on an unknown option or an option without a value
     */
    public static LaunchOptions parse(String... args) {
        String configName = DEFAULT_CONFIG;
        Path batchFile = null;
        for (String arg : args) {
            if (arg.startsWith(CONFIG)) {
                configName = valueOf(arg, CONFIG);
            } else if (arg.startsWith(BATCH)) {
                batchFile = Path.of(valueOf(arg, BATCH));
            } else if (!arg.startsWith(OPTION_PREFIX) && !arg.isBlank()) {
                configName = arg.strip();
            } else {
                throw new ConfigException("Unknown option '" + arg + "'. " + USAGE);
            }
        }
        return new LaunchOptions(configName, batchFile);
    }

    public String configName() {
        return configName;
    }

    public Optional<Path> batchFile() {
        return Optional.ofNullable(batchFile);
    }

    private static String valueOf(String arg, String option) {
        String value = arg.substring(option.length()).strip();
        if (value.isEmpty()) {
            throw new ConfigException("Option " + option + " needs a value. " + USAGE);
        }
        return value;
    }
}
