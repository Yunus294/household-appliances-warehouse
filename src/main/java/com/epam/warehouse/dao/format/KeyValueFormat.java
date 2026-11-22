package com.epam.warehouse.dao.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Records written as {@code key: value} lines, separated by blank lines:
 * <pre>
 * category: laptop
 * id: 101
 * brand: ASUS
 *
 * category: oven
 * id: 201
 * </pre>
 * Each record lists only its own keys, so records of different categories can share one file.
 * Lines starting with {@code #} are skipped.
 */
public final class KeyValueFormat implements RecordFormat {

    private static final char SEPARATOR = ':';
    private static final String COMMENT_PREFIX = "#";

    @Override
    public List<DataRow> read(BufferedReader reader) throws IOException {
        List<DataRow> rows = new ArrayList<>();
        Map<String, String> record = new LinkedHashMap<>();
        int recordLine = 0;
        int lineNumber = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.isBlank()) {
                addRecord(rows, record, recordLine);
                record = new LinkedHashMap<>();
                continue;
            }
            if (line.strip().startsWith(COMMENT_PREFIX)) {
                continue;
            }
            if (record.isEmpty()) {
                recordLine = lineNumber;
            }
            putPair(record, line, lineNumber);
        }
        addRecord(rows, record, recordLine);
        return rows;
    }

    private static void putPair(Map<String, String> record, String line, int lineNumber) {
        int separator = line.indexOf(SEPARATOR);
        if (separator <= 0) {
            throw new RecordFormatException("line " + lineNumber + ": expected 'key: value' but found '"
                    + line.strip() + "'");
        }
        String key = line.substring(0, separator).strip().toLowerCase(Locale.ROOT);
        String value = line.substring(separator + 1).strip();
        if (record.putIfAbsent(key, value) != null) {
            throw new RecordFormatException("line " + lineNumber + ": key '" + key + "' is repeated in one record");
        }
    }

    private static void addRecord(List<DataRow> rows, Map<String, String> record, int recordLine) {
        if (!record.isEmpty()) {
            rows.add(new DataRow(recordLine, record));
        }
    }
}
