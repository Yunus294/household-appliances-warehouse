package com.epam.warehouse.controller.argument;

import com.epam.warehouse.criteria.EqualsParameter;
import com.epam.warehouse.criteria.Parameter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * One value of a fixed list, e.g. {@code os=linux}. Case doesn't matter and "-" stands for "_",
 * so {@code chrome-os} means {@code CHROME_OS}.
 */
public final class EnumAttribute<T, E extends Enum<E>> extends Attribute<T, E> {

    private final Class<E> type;

    public EnumAttribute(String key, String description, Class<T> owner,
                         Function<? super T, ? extends E> getter, Class<E> type) {
        super(key, description, owner, getter);
        this.type = Objects.requireNonNull(type, "type");
    }

    @Override
    protected Parameter<T> parse(String value) throws InvalidRequestException {
        E constant;
        try {
            constant = Enum.valueOf(type, value.toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw invalid("has no value '" + value + "', use one of: " + choices());
        }
        return new EqualsParameter<>(key(), getter(), constant);
    }

    @Override
    protected String formatValue(E value) {
        return value.name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    @Override
    public String help() {
        return description() + ": " + choices();
    }

    /**
     * @return the allowed values, as the user types them
     */
    public String choices() {
        return Arrays.stream(type.getEnumConstants())
                .map(this::formatValue)
                .collect(Collectors.joining(", "));
    }
}
