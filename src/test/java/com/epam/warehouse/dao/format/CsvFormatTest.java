package com.epam.warehouse.dao.format;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvFormatTest {

    private static List<DataRow> read(String text) throws IOException {
        return new CsvFormat().read(new BufferedReader(new StringReader(text)));
    }

    @Test
    void readsValuesByColumnName() throws IOException {
        List<DataRow> rows = read("id;brand\n201;Bosch\n202;Samsung\n");

        assertEquals(2, rows.size());
        assertEquals("Bosch", rows.get(0).text("brand"));
        assertEquals(202, rows.get(1).longValue("id"));
    }

    @Test
    void rowsKeepTheirLineNumbers() throws IOException {
        List<DataRow> rows = read("# ovens\nid;brand\n\n201;Bosch\n# discontinued\n202;Samsung\n");

        assertEquals(4, rows.get(0).lineNumber());
        assertEquals(6, rows.get(1).lineNumber());
    }

    @Test
    void valuesAreTrimmed() throws IOException {
        assertEquals("Bosch", read("id ; brand\n201 ;  Bosch \n").get(0).text("brand"));
    }

    @Test
    void wrongNumberOfValuesNamesTheLine() {
        RecordFormatException e = assertThrows(RecordFormatException.class,
                () -> read("id;brand\n201;Bosch\n202;Samsung;extra\n"));
        assertEquals("line 3: expected 2 values but found 3", e.getMessage());
    }

    @Test
    void repeatedColumnIsRejected() {
        RecordFormatException e = assertThrows(RecordFormatException.class, () -> read("id;brand;ID\n"));
        assertEquals("line 1: column 'ID' is repeated", e.getMessage());
    }

    @Test
    void headerAloneMeansNoRecords() throws IOException {
        assertTrue(read("id;brand\n").isEmpty());
        assertTrue(read("").isEmpty());
    }

    @Test
    void byteOrderMarkIsIgnored() throws IOException {
        assertEquals(201, read("﻿id;brand\n201;Bosch\n").get(0).longValue("id"));
    }

    @Test
    void otherDelimiterCanBeUsed() throws IOException {
        List<DataRow> rows = new CsvFormat(",").read(new BufferedReader(new StringReader("id,brand\n201,Bosch\n")));

        assertEquals("Bosch", rows.get(0).text("brand"));
    }
}
