package com.epam.warehouse.criteria;

import java.util.Objects;

/**
 * A closed range of values, both ends included.
 *
 * @param from lower end
 * @param to   upper end, not less than {@code from}
 */
public record Range<T extends Comparable<? super T>>(T from, T to) {

    public Range {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException("Range start " + from + " is greater than its end " + to);
        }
    }

    public static <T extends Comparable<? super T>> Range<T> of(T from, T to) {
        return new Range<>(from, to);
    }

    /**
     * @return a range of one value
     */
    public static <T extends Comparable<? super T>> Range<T> exactly(T value) {
        return new Range<>(value, value);
    }

    public boolean contains(T value) {
        return value != null && from.compareTo(value) <= 0 && value.compareTo(to) <= 0;
    }
}
