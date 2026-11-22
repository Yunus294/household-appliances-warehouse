package com.epam.warehouse.view;

import com.epam.warehouse.controller.Request;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Checks the shape of a typed line and splits it into a {@link Request} (client-side validation).
 *
 * <p>{@code find laptops name="MacBook Air" price=500;900} becomes command {@code find},
 * argument {@code laptops} and options {@code name=MacBook Air}, {@code price=500;900}.
 *
 * <p>Only the form is checked here: quotes are closed, every {@code key=} has a value, no key is
 * repeated. Whether a command or a key exists is up to the controller.
 */
public final class CommandLineParser {

    private static final char QUOTE = '"';
    private static final char ASSIGN = '=';
    private static final Pattern NAME = Pattern.compile("[a-z][a-z0-9-]*");

    public Request parse(String line) throws InputSyntaxException {
        List<String> tokens = tokenize(line);
        if (tokens.isEmpty()) {
            throw new InputSyntaxException("Type a command, or 'help' to see them");
        }
        String command = tokens.get(0).toLowerCase(Locale.ROOT);
        if (!NAME.matcher(command).matches()) {
            throw new InputSyntaxException("'" + tokens.get(0) + "' is not a command name");
        }
        List<String> arguments = new ArrayList<>();
        Map<String, String> options = new LinkedHashMap<>();
        for (String token : tokens.subList(1, tokens.size())) {
            int assign = token.indexOf(ASSIGN);
            if (assign < 0) {
                arguments.add(token);
            } else {
                addOption(options, token.substring(0, assign), token.substring(assign + 1));
            }
        }
        return new Request(command, arguments, options);
    }

    private static void addOption(Map<String, String> options, String rawKey, String value)
            throws InputSyntaxException {
        String key = rawKey.toLowerCase(Locale.ROOT);
        if (!NAME.matcher(key).matches()) {
            throw new InputSyntaxException("'" + rawKey + "' is not a parameter name. Write key=value, e.g. price=500;900");
        }
        if (value.isBlank()) {
            throw new InputSyntaxException("Parameter '" + key + "' has no value");
        }
        if (options.putIfAbsent(key, value) != null) {
            throw new InputSyntaxException("Parameter '" + key + "' is given twice");
        }
    }

    /**
     * Splits the line on spaces, keeping quoted text together and dropping the quotes.
     */
    private static List<String> tokenize(String line) throws InputSyntaxException {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean quoted = false;
        boolean started = false;
        for (char c : line.toCharArray()) {
            if (c == QUOTE) {
                quoted = !quoted;
                started = true;
            } else if (Character.isWhitespace(c) && !quoted) {
                if (started) {
                    tokens.add(token.toString());
                    token.setLength(0);
                    started = false;
                }
            } else {
                token.append(c);
                started = true;
            }
        }
        if (quoted) {
            throw new InputSyntaxException("A closing quote (\") is missing");
        }
        if (started) {
            tokens.add(token.toString());
        }
        return tokens;
    }
}
