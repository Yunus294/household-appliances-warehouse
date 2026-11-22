package com.epam.warehouse.dao;

import com.epam.warehouse.common.InstanceHolder;
import com.epam.warehouse.entity.Appliance;

/**
 * Entry point of the DAO layer: the registry chosen at startup. {@link #init} works once; later
 * calls are ignored.
 */
public final class DaoFactory {

    private static final InstanceHolder<DaoRegistry> REGISTRY = new InstanceHolder<>("DAO registry");

    private DaoFactory() {
    }

    public static void init(DaoRegistry registry) {
        REGISTRY.init(registry);
    }

    public static DaoRegistry getInstance() {
        return REGISTRY.get();
    }

    /**
     * @throws DaoException if no DAO is registered for the type
     */
    public static <A extends Appliance> ApplianceDao<A> getInstance(Class<A> type) throws DaoException {
        return REGISTRY.get().get(type);
    }
}
