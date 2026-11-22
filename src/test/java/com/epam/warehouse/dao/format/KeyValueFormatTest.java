package com.epam.warehouse.dao.format;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeyValueFormatTest {

    private static List<DataRow> read(String text) throws IOException {
        return new KeyValueFormat().read(new BufferedReader(new StringReader(text)));
    }

    @Test
    void blankLinesSeparateRecords() throws IOException {
        List<DataRow> rows = read("category: oven\nid: 201\n\n\ncategory: laptop\nid: 101\nos: linux");

        assertEquals(2, rows.size());
        assertEquals("oven", rows.get(0).text("category"));
        assertEquals("linux", rows.get(1).text("os"));
    }

    @Test
    void eachRecordHasOnlyItsOwnKeys() throws IOException {
        List<DataRow> rows = read("id: 201\npower: 3400\n\nid: 101\nos: linux\n");

        assertFalse(rows.get(0).find("os").isPresent());
        assertFalse(rows.get(1).find("power").isPresent());
    }

    @Test
    void recordStartsOnItsFirstKeyLine() throws IOException {
        List<DataRow> rows = read("# stock\n\nid: 201\n\n# next\nid: 202\n");

        assertEquals(3, rows.get(0).lineNumber());
        assertEquals(6, rows.get(1).lineNumber());
    }

    @Test
    void valueMayContainTheSeparator() throws IOException {
        assertEquals("Serie 4: built-in", read("model: Serie 4: built-in\n").get(0).text("model"));
    }

    @Test
    void lineWithoutSeparatorIsRejected() {
        RecordFormatException e = assertThrows(RecordFormatException.class, () -> read("id: 201\nBosch\n"));
        assertEquals("line 2: expected 'key: value' but found 'Bosch'", e.getMessage());
    }

    @Test
    void repeatedKeyIsRejected() {
        RecordFormatException e = assertThrows(RecordFormatException.class, () -> read("id: 201\nID: 202\n"));
        assertEquals("line 2: key 'id' is repeated in one record", e.getMessage());
    }
}
