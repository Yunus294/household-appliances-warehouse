package com.epam.warehouse.criteria;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * "The text contains this fragment", ignoring case: {@code cpu=intel} matches "Intel Core i5".
 *
 * @param name     what is compared, for messages and {@code toString()}
 * @param getter   reads the text from an item
 * @param fragment what to look for
 */
public record TextParameter<T>(String name, Function<? super T, ? extends String> getter, String fragment)
        implements Parameter<T> {

    public TextParameter {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(getter, "getter");
        if (fragment == null || fragment.isBlank()) {
            throw new IllegalArgumentException(name + " to search for must not be empty");
        }
        fragment = fragment.strip().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(T item) {
        String text = getter.apply(item);
        return text != null && text.toLowerCase(Locale.ROOT).contains(fragment);
    }

    @Override
    public String toString() {
        return name + " contains '" + fragment + "'";
    }
}
