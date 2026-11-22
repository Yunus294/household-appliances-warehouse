package com.epam.warehouse.criteria;

import java.util.Objects;
import java.util.function.Function;

/**
 * "The value lies within the range", e.g. price from 500 to 900.
 *
 * @param name   what is compared, for messages and {@code toString()}
 * @param getter reads the value from an item
 * @param range  allowed values
 */
public record RangeParameter<T, V extends Comparable<? super V>>(
        String name, Function<? super T, ? extends V> getter, Range<V> range) implements Parameter<T> {

    public RangeParameter {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(getter, "getter");
        Objects.requireNonNull(range, "range");
    }

    @Override
    public boolean test(T item) {
        return range.contains(getter.apply(item));
    }

    @Override
    public String toString() {
        return name + " in [" + range.from() + ", " + range.to() + "]";
    }
}
