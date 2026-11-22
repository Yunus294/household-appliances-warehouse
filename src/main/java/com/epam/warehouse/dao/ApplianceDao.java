package com.epam.warehouse.dao;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.entity.Appliance;

import java.util.List;

/**
 * Access to the stored appliances of one category.
 *
 * @param <A> the appliance type
 */
@FunctionalInterface
public interface ApplianceDao<A extends Appliance> {

    /**
     * @return the stored appliances that meet the criteria, in storage order
     * @throws DaoException if the data can't be read
     */
    List<A> find(SearchCriteria<? super A> criteria) throws DaoException;
}
