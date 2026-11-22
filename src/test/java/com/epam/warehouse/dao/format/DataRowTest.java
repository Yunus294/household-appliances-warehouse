package com.epam.warehouse.dao.format;

import com.epam.warehouse.entity.Laptop;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataRowTest {

    private static DataRow row(String column, String value) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put(column, value);
        return new DataRow(7, values);
    }

    @Test
    void readsTypedValues() {
        assertEquals(649L, row("price", "649").longValue("price"));
        assertEquals(16, row("ram", "16").intValue("ram"));
        assertEquals(15.6, row("screen", "15.6").doubleValue("screen"));
        assertEquals(Laptop.Os.CHROME_OS, row("os", "Chrome-OS").enumValue("os", Laptop.Os.class));
    }

    @Test
    void readsYesNoAndTrueFalse() {
        assertTrue(row("nofrost", "Yes").booleanValue("nofrost"));
        assertTrue(row("nofrost", "true").booleanValue("nofrost"));
        assertFalse(row("nofrost", "no").booleanValue("nofrost"));
        assertFalse(row("nofrost", "FALSE").booleanValue("nofrost"));
    }

    @Test
    void columnNamesIgnoreCase() {
        assertEquals("Bosch", row("Brand", "Bosch").text("brand"));
    }

    @Test
    void missingColumnIsNamed() {
        RecordFormatException e = assertThrows(RecordFormatException.class, () -> row("id", "1").text("price"));
        assertEquals("column 'price' is missing", e.getMessage());
    }

    @Test
    void emptyValueIsReported() {
        RecordFormatException e = assertThrows(RecordFormatException.class, () -> row("price", " ").longValue("price"));
        assertEquals("'price' is empty", e.getMessage());
    }

    @Test
    void wrongNumberIsQuoted() {
        RecordFormatException e = assertThrows(RecordFormatException.class,
                () -> row("price", "450.00").longValue("price"));
        assertEquals("'price' must be a whole number, but was '450.00'", e.getMessage());
    }

    @Test
    void unknownEnumValueListsTheChoices() {
        RecordFormatException e = assertThrows(RecordFormatException.class,
                () -> row("os", "winxp").enumValue("os", Laptop.Os.class));
        assertEquals("'os' has unknown value 'winxp', expected one of: windows, macos, linux, chrome-os", e.getMessage());
    }

    @Test
    void wrongBooleanIsRejected() {
        assertThrows(RecordFormatException.class, () -> row("nofrost", "maybe").booleanValue("nofrost"));
    }

    @Test
    void findSkipsMissingAndBlankValues() {
        assertEquals(Optional.of("laptop"), row("category", "laptop").find("category"));
        assertEquals(Optional.empty(), row("category", "").find("category"));
        assertEquals(Optional.empty(), row("id", "1").find("category"));
    }
}
