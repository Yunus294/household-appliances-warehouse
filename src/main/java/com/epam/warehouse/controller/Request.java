package com.epam.warehouse.controller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A request to the controller, already split into parts.
 *
 * <p>{@code find laptops ram=16 price=500;900} becomes command {@code find}, arguments
 * {@code [laptops]} and options {@code {ram=16, price=500;900}}. The command and the option keys
 * are lower-cased; values stay as typed.
 *
 * @param command   what to do
 * @param arguments plain words after the command
 * @param options   {@code key=value} pairs, in the order given
 */
public record Request(String command, List<String> arguments, Map<String, String> options) {

    public Request {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("Command must not be empty");
        }
        command = command.strip().toLowerCase(Locale.ROOT);
        arguments = List.copyOf(arguments);
        Map<String, String> normalized = new LinkedHashMap<>();
        options.forEach((key, value) ->
                normalized.put(key.strip().toLowerCase(Locale.ROOT), Objects.requireNonNull(value, key)));
        options = Collections.unmodifiableMap(normalized);
    }

    /**
     * @return a request of a bare command, e.g. {@code help}
     */
    public static Request of(String command) {
        return new Request(command, List.of(), Map.of());
    }
}
