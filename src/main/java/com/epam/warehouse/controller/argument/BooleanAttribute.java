package com.epam.warehouse.controller.argument;

import com.epam.warehouse.criteria.EqualsParameter;
import com.epam.warehouse.criteria.Parameter;

import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

/**
 * Yes or no, e.g. {@code nofrost=yes}. Also accepts true and false.
 */
public final class BooleanAttribute<T> extends Attribute<T, Boolean> {

    private static final String YES = "yes";
    private static final String NO = "no";
    private static final Set<String> TRUE_WORDS = Set.of(YES, "true");
    private static final Set<String> FALSE_WORDS = Set.of(NO, "false");

    public BooleanAttribute(String key, String description, Class<T> owner, Function<? super T, Boolean> getter) {
        super(key, description, owner, getter);
    }

    @Override
    protected Parameter<T> parse(String value) throws InvalidRequestException {
        String word = value.toLowerCase(Locale.ROOT);
        if (TRUE_WORDS.contains(word)) {
            return new EqualsParameter<>(key(), getter(), Boolean.TRUE);
        }
        if (FALSE_WORDS.contains(word)) {
            return new EqualsParameter<>(key(), getter(), Boolean.FALSE);
        }
        throw invalid("must be yes or no, but was '" + value + "'");
    }

    @Override
    public String help() {
        return description() + ": " + YES + " or " + NO;
    }

    @Override
    protected String formatValue(Boolean value) {
        return value ? YES : NO;
    }
}
