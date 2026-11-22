package com.epam.warehouse.controller.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A plain-text table with a header and aligned columns:
 * <pre>
 * ID   Name              Price $
 * ---  ----------------  -------
 * 101  ASUS Vivobook 15      649
 * </pre>
 */
public final class TextTable {

    private static final String COLUMN_GAP = "  ";
    private static final String RULE = "-";
    private static final String NEW_LINE = "\n";

    private final List<String> headers;
    private final boolean[] rightAligned;
    private final List<List<String>> rows = new ArrayList<>();

    public TextTable(String... headers) {
        if (headers.length == 0) {
            throw new IllegalArgumentException("A table needs at least one column");
        }
        this.headers = List.of(headers);
        this.rightAligned = new boolean[headers.length];
    }

    /**
     * Right-aligns the columns with these indexes (0 is the first column); for numbers.
     */
    public TextTable alignRight(int... columns) {
        for (int column : columns) {
            rightAligned[column] = true;
        }
        return this;
    }

    public TextTable addRow(String... cells) {
        if (cells.length != headers.size()) {
            throw new IllegalArgumentException("Expected " + headers.size() + " cells but got " + cells.length);
        }
        rows.add(List.of(cells));
        return this;
    }

    public String render() {
        int[] widths = widths();
        List<String> lines = new ArrayList<>();
        lines.add(line(headers, widths));
        lines.add(line(rules(widths), widths));
        for (List<String> row : rows) {
            lines.add(line(row, widths));
        }
        return String.join(NEW_LINE, lines);
    }

    private int[] widths() {
        int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < widths.length; i++) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }
        return widths;
    }

    private static List<String> rules(int[] widths) {
        List<String> rules = new ArrayList<>();
        for (int width : widths) {
            rules.add(String.join("", Collections.nCopies(width, RULE)));
        }
        return rules;
    }

    private String line(List<String> cells, int[] widths) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                line.append(COLUMN_GAP);
            }
            line.append(pad(cells.get(i), widths[i], rightAligned[i]));
        }
        return line.toString().stripTrailing();
    }

    private static String pad(String text, int width, boolean right) {
        String padding = " ".repeat(width - text.length());
        return right ? padding + text : text + padding;
    }
}
