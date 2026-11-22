package com.epam.warehouse.controller.argument;

import com.epam.warehouse.controller.format.Numbers;
import com.epam.warehouse.criteria.Parameter;
import com.epam.warehouse.criteria.Range;
import com.epam.warehouse.criteria.RangeParameter;

import java.util.Objects;
import java.util.function.Function;

/**
 * A number. Takes one value ({@code ram=16}) or a range with both ends included
 * ({@code price=500;900}).
 *
 * @param <N> Integer, Long or Double
 */
public final class NumberAttribute<T, N extends Number & Comparable<N>> extends Attribute<T, N> {

    private static final String RANGE_SEPARATOR = ";";

    private final Function<String, N> parser;
    private final String kind;

    /**
     * @param parser text to number, throwing {@link NumberFormatException} on bad text
     * @param kind   what a valid value is, for messages: "a whole number", "a number"
     */
    public NumberAttribute(String key, String description, Class<T> owner,
                           Function<? super T, ? extends N> getter, Function<String, N> parser, String kind) {
        super(key, description, owner, getter);
        this.parser = Objects.requireNonNull(parser, "parser");
        this.kind = Objects.requireNonNull(kind, "kind");
    }

    @Override
    protected Parameter<T> parse(String value) throws InvalidRequestException {
        String[] ends = value.split(RANGE_SEPARATOR, -1);
        if (ends.length > 2) {
            throw invalid("takes one value or a range from;to, e.g. 500;900");
        }
        N from = number(ends[0]);
        if (ends.length == 1) {
            return new RangeParameter<>(key(), getter(), Range.exactly(from));
        }
        N to = number(ends[1]);
        if (from.compareTo(to) > 0) {
            throw invalid("range start " + from + " is greater than its end " + to);
        }
        return new RangeParameter<>(key(), getter(), Range.of(from, to));
    }

    private N number(String text) throws InvalidRequestException {
        String trimmed = text.strip();
        if (trimmed.isEmpty()) {
            throw invalid("needs a value on both sides of ';', e.g. 500;900");
        }
        N number;
        try {
            number = parser.apply(trimmed);
        } catch (NumberFormatException e) {
            throw invalid("must be " + kind + ", but was '" + trimmed + "'");
        }
        if (!Double.isFinite(number.doubleValue())) {
            throw invalid("must be " + kind + ", but was '" + trimmed + "'");
        }
        if (number.doubleValue() < 0) {
            throw invalid("can't be negative, but was " + trimmed);
        }
        return number;
    }

    @Override
    protected String formatValue(N value) {
        return value instanceof Double decimal ? Numbers.decimal(decimal) : value.toString();
    }
}
