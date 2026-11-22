package com.epam.warehouse.criteria;

import java.util.Objects;
import java.util.function.Function;

/**
 * "The value equals this one", e.g. os is LINUX.
 *
 * @param name     what is compared, for messages and {@code toString()}
 * @param getter   reads the value from an item
 * @param expected the wanted value
 */
public record EqualsParameter<T, V>(String name, Function<? super T, ? extends V> getter, V expected)
        implements Parameter<T> {

    public EqualsParameter {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(getter, "getter");
        Objects.requireNonNull(expected, "expected");
    }

    @Override
    public boolean test(T item) {
        return expected.equals(getter.apply(item));
    }

    @Override
    public String toString() {
        return name + " = " + expected;
    }
}
