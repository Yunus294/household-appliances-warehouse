package com.epam.warehouse.controller;

import java.util.List;
import java.util.Map;

/**
 * Requests for tests, written out explicitly.
 */
public final class TestRequests {

    private TestRequests() {
    }

    public static Request request(String command, String target) {
        return new Request(command, List.of(target), Map.of());
    }

    public static Request request(String command, String target, Map<String, String> options) {
        return new Request(command, List.of(target), options);
    }
}
