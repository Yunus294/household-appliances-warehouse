package com.epam.warehouse.controller.argument;

import com.epam.warehouse.criteria.Parameter;
import com.epam.warehouse.criteria.TextParameter;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Text. Matches any part of the value, ignoring case: {@code cpu=i5} finds "Intel Core i5-1235U".
 */
public final class TextAttribute<T> extends Attribute<T, String> {

    private static final String QUOTE = "\"";

    public TextAttribute(String key, String description, Class<T> owner, Function<? super T, String> getter) {
        super(key, description, owner, getter);
    }

    @Override
    protected Parameter<T> parse(String value) throws InvalidRequestException {
        if (value.isEmpty()) {
            throw invalid("needs some text to look for");
        }
        return new TextParameter<>(key(), getter(), value);
    }

    @Override
    protected Comparator<String> valueOrder() {
        return String.CASE_INSENSITIVE_ORDER;
    }

    /**
     * Quotes values with spaces, so they can be copied straight into a search.
     */
    @Override
    protected String formatValue(String value) {
        return value.contains(" ") ? QUOTE + value + QUOTE : value;
    }
}
