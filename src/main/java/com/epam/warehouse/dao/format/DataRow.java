package com.epam.warehouse.dao.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * One record of a data file: column name → text value, plus the line it starts on.
 *
 * <p>The typed getters turn text into values and explain exactly what's wrong when they can't,
 * e.g. "'price' must be a whole number, but was '450.00'".
 */
public final class DataRow {

    private static final Set<String> YES = Set.of("yes", "true");
    private static final Set<String> NO = Set.of("no", "false");

    private final int lineNumber;
    private final Map<String, String> values;

    /**
     * @param values column names are compared ignoring case; values are trimmed
     */
    public DataRow(int lineNumber, Map<String, String> values) {
        this.lineNumber = lineNumber;
        Map<String, String> normalized = new LinkedHashMap<>();
        values.forEach((column, value) ->
                normalized.put(normalize(column), value == null ? "" : value.strip()));
        this.values = Collections.unmodifiableMap(normalized);
    }

    public int lineNumber() {
        return lineNumber;
    }

    /**
     * @return the value, or empty if the column is missing or blank
     */
    public Optional<String> find(String column) {
        return Optional.ofNullable(values.get(normalize(column))).filter(value -> !value.isEmpty());
    }

    /**
     * @throws RecordFormatException if the column is missing or empty
     */
    public String text(String column) {
        String value = values.get(normalize(column));
        if (value == null) {
            throw new RecordFormatException("column '" + column + "' is missing");
        }
        if (value.isEmpty()) {
            throw new RecordFormatException("'" + column + "' is empty");
        }
        return value;
    }

    public long longValue(String column) {
        return number(column, Long::parseLong, "a whole number");
    }

    public int intValue(String column) {
        return number(column, Integer::parseInt, "a whole number");
    }

    public double doubleValue(String column) {
        return number(column, Double::parseDouble, "a number");
    }

    /**
     * Accepts yes/no and true/false.
     */
    public boolean booleanValue(String column) {
        String value = text(column).toLowerCase(Locale.ROOT);
        if (YES.contains(value)) {
            return true;
        }
        if (NO.contains(value)) {
            return false;
        }
        throw new RecordFormatException("'" + column + "' must be yes or no, but was '" + value + "'");
    }

    /**
     * Accepts the constant name in any case, with "-" for "_": {@code chrome-os} → {@code CHROME_OS}.
     */
    public <E extends Enum<E>> E enumValue(String column, Class<E> type) {
        String value = text(column);
        String constant = value.toUpperCase(Locale.ROOT).replace('-', '_');
        try {
            return Enum.valueOf(type, constant);
        } catch (IllegalArgumentException e) {
            throw new RecordFormatException("'" + column + "' has unknown value '" + value
                    + "', expected one of: " + namesOf(type));
        }
    }

    private <N> N number(String column, Function<String, N> parser, String expected) {
        String value = text(column);
        try {
            return parser.apply(value);
        } catch (NumberFormatException e) {
            throw new RecordFormatException("'" + column + "' must be " + expected + ", but was '" + value + "'");
        }
    }

    private static <E extends Enum<E>> String namesOf(Class<E> type) {
        return Arrays.stream(type.getEnumConstants())
                .map(constant -> constant.name().toLowerCase(Locale.ROOT).replace('_', '-'))
                .collect(Collectors.joining(", "));
    }

    private static String normalize(String column) {
        return column.strip().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return "line " + lineNumber + " " + values;
    }
}
