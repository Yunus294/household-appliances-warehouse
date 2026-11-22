package com.epam.warehouse.dao.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Delimited text: the first line names the columns, every other line is a record.
 * <pre>
 * id;brand;model;price
 * 101;ASUS;Vivobook 15;649
 * </pre>
 * Columns are matched by name, so their order doesn't matter. Blank lines and lines starting with
 * {@code #} are skipped.
 */
public final class CsvFormat implements RecordFormat {

    private static final String DEFAULT_DELIMITER = ";";
    private static final String COMMENT_PREFIX = "#";
    private static final char BYTE_ORDER_MARK = '﻿';

    private final Pattern delimiter;

    public CsvFormat() {
        this(DEFAULT_DELIMITER);
    }

    public CsvFormat(String delimiter) {
        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter must not be empty");
        }
        this.delimiter = Pattern.compile(Pattern.quote(delimiter));
    }

    @Override
    public List<DataRow> read(BufferedReader reader) throws IOException {
        List<DataRow> rows = new ArrayList<>();
        List<String> header = null;
        int lineNumber = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (lineNumber == 1 && !line.isEmpty() && line.charAt(0) == BYTE_ORDER_MARK) {
                line = line.substring(1);
            }
            if (isSkipped(line)) {
                continue;
            }
            List<String> cells = split(line);
            if (header == null) {
                header = checkHeader(cells, lineNumber);
            } else {
                rows.add(toRow(header, cells, lineNumber));
            }
        }
        return rows;
    }

    private static boolean isSkipped(String line) {
        return line.isBlank() || line.strip().startsWith(COMMENT_PREFIX);
    }

    private List<String> split(String line) {
        return Arrays.stream(delimiter.split(line, -1)).map(String::strip).toList();
    }

    private static List<String> checkHeader(List<String> columns, int lineNumber) {
        Set<String> seen = new HashSet<>();
        for (String column : columns) {
            if (column.isEmpty()) {
                throw new RecordFormatException("line " + lineNumber + ": header has an empty column name");
            }
            if (!seen.add(column.toLowerCase())) {
                throw new RecordFormatException("line " + lineNumber + ": column '" + column + "' is repeated");
            }
        }
        return columns;
    }

    private static DataRow toRow(List<String> header, List<String> cells, int lineNumber) {
        if (cells.size() != header.size()) {
            throw new RecordFormatException("line " + lineNumber + ": expected " + header.size()
                    + " values but found " + cells.size());
        }
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < header.size(); i++) {
            values.put(header.get(i), cells.get(i));
        }
        return new DataRow(lineNumber, values);
    }
}
