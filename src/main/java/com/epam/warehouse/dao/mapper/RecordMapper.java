package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.dao.format.RecordFormatException;
import com.epam.warehouse.entity.Appliance;

/**
 * Turns a data row into an appliance of one type.
 *
 * @param <A> the appliance type
 */
public interface RecordMapper<A extends Appliance> {

    /**
     * @return the type this mapper creates
     */
    Class<A> type();

    /**
     * @throws RecordFormatException    if a column is missing or holds text of the wrong kind
     * @throws IllegalArgumentException if a value is impossible, e.g. a zero price
     */
    A map(DataRow row);
}
