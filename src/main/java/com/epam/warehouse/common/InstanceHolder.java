package com.epam.warehouse.common;

import java.util.Objects;

/**
 * Keeps the one configured implementation of a layer.
 *
 * <p>Every layer factory ({@code DaoFactory}, {@code ServiceFactory}, {@code ControllerFactory},
 * {@code ViewFactory}) delegates to an {@code InstanceHolder}, so the "initialise once, then hand
 * out" logic is written once instead of being copied into each factory.
 *
 * @param <T> type of the held instance
 */
public final class InstanceHolder<T> {

    private final String name;
    private T instance;

    /**
     * @param name what is held, used in error messages
     */
    public InstanceHolder(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Stores the instance if none is stored yet. Later calls are ignored, so the implementation
     * chosen at startup can't be replaced by accident.
     *
     * @return {@code true} if the instance was stored, {@code false} if one was already there
     */
    public synchronized boolean init(T value) {
        Objects.requireNonNull(value, name);
        if (instance != null) {
            return false;
        }
        instance = value;
        return true;
    }

    /**
     * @return the stored instance
     * @throws IllegalStateException if {@link #init} hasn't been called yet
     */
    public synchronized T get() {
        if (instance == null) {
            throw new IllegalStateException(name + " is not initialized");
        }
        return instance;
    }
}
