package com.epam.warehouse.criteria;

import java.util.Objects;

/**
 * One search condition, e.g. "price from 500 to 900" or "cpu contains Intel".
 *
 * @param <T> what the condition can be checked against: {@code Appliance}, {@code Laptop},
 *            {@code PowerConsumable}...
 */
@FunctionalInterface
public interface Parameter<T> {

    /**
     * @return {@code true} if the item meets the condition
     */
    boolean test(T item);

    /**
     * @return a condition every item meets
     */
    static <T> Parameter<T> any() {
        return item -> true;
    }

    /**
     * @return a condition no item meets
     */
    static <T> Parameter<T> none() {
        return item -> false;
    }

    /**
     * Makes a condition written for one type (say {@code Laptop}) usable on any item: items of
     * other types just don't match. That's how "os=linux" works in a search over all categories.
     *
     * @param type      the type the condition is written for
     * @param parameter the condition
     */
    static <S> Parameter<Object> onlyFor(Class<S> type, Parameter<? super S> parameter) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(parameter, "parameter");
        return item -> type.isInstance(item) && parameter.test(type.cast(item));
    }
}
