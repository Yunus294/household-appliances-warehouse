package com.epam.warehouse.controller.command;

import java.util.Locale;
import java.util.Optional;

/**
 * The commands, with how to write them and what they do.
 */
public enum CommandName {

    FIND("find <target> [key=value ...] [sort=key] [order=asc|desc]", "search the stock"),
    LIST("list <target> [sort=key] [order=asc|desc]", "everything in the target, sorted"),
    COST("cost <target> [key=value ...]", "units in stock and what they are worth"),
    HELP("help", "show this help"),
    EXIT("exit", "quit");

    private final String usage;
    private final String description;

    CommandName(String usage, String description) {
        this.usage = usage;
        this.description = description;
    }

    /**
     * @return the word the user types, e.g. "find"
     */
    public String token() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String usage() {
        return usage;
    }

    public String description() {
        return description;
    }

    /**
     * @param token ignoring case
     */
    public static Optional<CommandName> byToken(String token) {
        for (CommandName name : values()) {
            if (name.token().equalsIgnoreCase(token)) {
                return Optional.of(name);
            }
        }
        return Optional.empty();
    }
}
