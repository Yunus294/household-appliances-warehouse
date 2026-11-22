package com.epam.warehouse.dao;

import com.epam.warehouse.entity.Appliance;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The DAO of each appliance type.
 *
 * <p>{@link #register} only accepts a DAO of the same type as its key, so the cast in {@link #get}
 * can never fail.
 */
public final class DaoRegistry {

    private final Map<Class<? extends Appliance>, ApplianceDao<? extends Appliance>> daos =
            new LinkedHashMap<>();

    public <A extends Appliance> DaoRegistry register(Class<A> type, ApplianceDao<A> dao) {
        daos.put(Objects.requireNonNull(type, "type"), Objects.requireNonNull(dao, "dao"));
        return this;
    }

    /**
     * @throws DaoException if no DAO is registered for the type
     */
    @SuppressWarnings("unchecked") // safe: register() pairs Class<A> with ApplianceDao<A> only
    public <A extends Appliance> ApplianceDao<A> get(Class<A> type) throws DaoException {
        ApplianceDao<? extends Appliance> dao = daos.get(type);
        if (dao == null) {
            throw new DaoException("No data source is configured for " + type.getSimpleName());
        }
        return (ApplianceDao<A>) dao;
    }

    /**
     * @return the registered types, in registration order
     */
    public Set<Class<? extends Appliance>> types() {
        return Collections.unmodifiableSet(daos.keySet());
    }
}
