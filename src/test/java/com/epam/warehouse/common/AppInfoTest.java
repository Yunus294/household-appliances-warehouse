package com.epam.warehouse.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppInfoTest {

    @Test
    void everyFieldIsRequired() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new AppInfo("Warehouse", "1.0", "2026-11-22", " ", "me@example.com"));
        assertEquals("Application developer must not be empty", e.getMessage());
    }
}
