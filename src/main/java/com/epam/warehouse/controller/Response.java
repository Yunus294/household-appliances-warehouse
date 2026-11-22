package com.epam.warehouse.controller;

/**
 * The controller's answer.
 *
 * @param text what to show the user
 * @param ok   {@code false} if the request failed
 * @param out  {@code true} if the application should stop
 */
public record Response(String text, boolean ok, boolean out) {

    /**
     * The request was done.
     */
    public static Response ok(String text) {
        return new Response(text, true, false);
    }

    /**
     * The request was wrong; the user can try again.
     */
    public static Response error(String text) {
        return new Response(text, false, false);
    }

    /**
     * The user asked to quit.
     */
    public static Response exit(String text) {
        return new Response(text, true, true);
    }

    /**
     * Something below the controller broke; the application can't go on.
     */
    public static Response failure(String text) {
        return new Response(text, false, true);
    }
}
