package com.epam.warehouse.dao;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.entity.Appliance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reads the source on every search, so changes to the data file are picked up without a restart.
 *
 * @param <A> the appliance type
 */
public final class ApplianceDaoImpl<A extends Appliance> implements ApplianceDao<A> {

    private final Source<A> source;

    public ApplianceDaoImpl(Source<A> source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    public List<A> find(SearchCriteria<? super A> criteria) throws DaoException {
        Objects.requireNonNull(criteria, "criteria");
        List<A> found = new ArrayList<>();
        for (A appliance : source.readAll()) {
            if (criteria.test(appliance)) {
                found.add(appliance);
            }
        }
        return found;
    }

    @Override
    public String toString() {
        return "ApplianceDaoImpl{" + source.name() + "}";
    }
}
