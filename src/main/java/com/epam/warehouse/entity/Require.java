package com.epam.warehouse.entity;

/**
 * Argument checks shared by the entity builders.
 */
final class Require {

    private Require() {
    }

    static long positive(long value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive, but was " + value);
        }
        return value;
    }

    static int positive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive, but was " + value);
        }
        return value;
    }

    static double positive(double value, String field) {
        if (!(value > 0) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(field + " must be a positive number, but was " + value);
        }
        return value;
    }

    static int notNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " can't be negative, but was " + value);
        }
        return value;
    }

    static String notBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        return value.strip();
    }

    static <T> T notNull(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        return value;
    }

    /**
     * Used by {@code build()}: a builder field that was never set.
     */
    static void present(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " is required");
        }
    }
}
