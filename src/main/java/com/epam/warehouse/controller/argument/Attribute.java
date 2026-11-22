package com.epam.warehouse.controller.argument;

import com.epam.warehouse.criteria.Parameter;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A property of appliances the user can search and sort by, e.g. {@code price} or {@code ram}.
 *
 * <p>One attribute knows everything about its key: which appliances have it, how to read it, how
 * to turn the user's text into a search {@link Parameter}, how to compare and how to print it.
 * So adding a searchable property is one line in {@link Attributes}.
 *
 * @param <T> the type that has the property: {@code Appliance}, {@code Laptop},
 *            {@code PowerConsumable}...
 * @param <V> the type of the value
 */
public abstract class Attribute<T, V extends Comparable<? super V>> {

    private final String key;
    private final String description;
    private final Class<T> owner;
    private final Function<? super T, ? extends V> getter;

    protected Attribute(String key, String description, Class<T> owner, Function<? super T, ? extends V> getter) {
        this.key = Objects.requireNonNull(key, "key");
        this.description = Objects.requireNonNull(description, "description");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.getter = Objects.requireNonNull(getter, "getter");
    }

    public String key() {
        return key;
    }

    /**
     * @return what the value means, for the help
     */
    public String description() {
        return description;
    }

    /**
     * @return the line of the help about this attribute; subclasses add the allowed values
     */
    public String help() {
        return description;
    }

    public boolean appliesTo(Category category) {
        return owner.isAssignableFrom(category.getType());
    }

    public boolean appliesTo(Appliance appliance) {
        return owner.isInstance(appliance);
    }

    /**
     * @return {@code true} if every appliance has the property (price, brand...)
     */
    public boolean isCommon() {
        return owner.isAssignableFrom(Appliance.class);
    }

    /**
     * @param value the user's text, e.g. {@code 500;900}
     * @return a search condition; appliances that don't have this property never meet it
     * @throws InvalidRequestException if the text isn't a valid value
     */
    public final Parameter<Object> parameter(String value) throws InvalidRequestException {
        return Parameter.onlyFor(owner, parse(value.strip()));
    }

    /**
     * Orders appliances by this property. Appliances that don't have it go last in both directions.
     */
    public final Comparator<Appliance> comparator(boolean descending) {
        Comparator<V> values = descending ? valueOrder().reversed() : valueOrder();
        return Comparator.comparing(this::valueOrNull, Comparator.nullsLast(values));
    }

    /**
     * @return the value as the user would type it, or empty if the appliance doesn't have it
     */
    public final Optional<String> format(Appliance appliance) {
        V value = valueOrNull(appliance);
        return value == null ? Optional.empty() : Optional.of(formatValue(value));
    }

    protected abstract Parameter<T> parse(String value) throws InvalidRequestException;

    protected Comparator<V> valueOrder() {
        return Comparator.naturalOrder();
    }

    protected String formatValue(V value) {
        return String.valueOf(value);
    }

    protected final Function<? super T, ? extends V> getter() {
        return getter;
    }

    /**
     * @return an exception with a message that starts with the key, e.g. "'price' can't be negative"
     */
    protected final InvalidRequestException invalid(String problem) {
        return new InvalidRequestException("'" + key + "' " + problem);
    }

    private V valueOrNull(Appliance appliance) {
        return owner.isInstance(appliance) ? getter.apply(owner.cast(appliance)) : null;
    }

    @Override
    public String toString() {
        return key;
    }
}
