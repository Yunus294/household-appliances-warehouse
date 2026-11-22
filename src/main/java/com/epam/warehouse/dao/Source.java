package com.epam.warehouse.dao;

import com.epam.warehouse.entity.Appliance;

import java.util.List;

/**
 * Where the appliances of one category come from.
 *
 * @param <A> the appliance type
 */
public interface Source<A extends Appliance> {

    /**
     * @return a short description for messages, e.g. the file name
     */
    String name();

    /**
     * @return every appliance in the source
     * @throws DaoException if the source can't be read or has a broken record
     */
    List<A> readAll() throws DaoException;
}
