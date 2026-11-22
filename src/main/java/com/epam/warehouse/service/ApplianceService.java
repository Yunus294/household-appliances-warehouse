package com.epam.warehouse.service;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * What the warehouse can answer about its stock.
 */
public interface ApplianceService {

    /**
     * Finds appliances across one or more categories.
     *
     * @param categories where to look
     * @param criteria   what the appliances must meet; empty criteria match everything
     * @param order      how to sort the result; appliances that compare equal are sorted by id
     * @return the matching appliances, sorted
     * @throws ServiceException         if the stock data is unavailable
     * @throws IllegalArgumentException if an argument is {@code null}
     */
    List<Appliance> find(Collection<Category> categories, SearchCriteria<Appliance> criteria,
                         Comparator<Appliance> order) throws ServiceException;

    /**
     * Counts the matching appliances and the value of their stock, per category.
     *
     * @throws ServiceException         if the stock data is unavailable
     * @throws IllegalArgumentException if an argument is {@code null}
     */
    StockReport stockReport(Collection<Category> categories, SearchCriteria<Appliance> criteria)
            throws ServiceException;
}
