package com.epam.warehouse.service;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.dao.DaoException;
import com.epam.warehouse.dao.DaoRegistry;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Searches and totals the stock through the DAO layer.
 *
 * <p>The DAO layer works with one category at a time; this service is where several categories
 * are combined, sorted and summed up.
 */
public final class ApplianceServiceImpl implements ApplianceService {

    private static final Comparator<Appliance> BY_ID = Comparator.comparingLong(Appliance::getId);

    private final DaoRegistry daos;

    public ApplianceServiceImpl(DaoRegistry daos) {
        this.daos = requireArgument(daos, "daos");
    }

    @Override
    public List<Appliance> find(Collection<Category> categories, SearchCriteria<Appliance> criteria,
                                Comparator<Appliance> order) throws ServiceException {
        requireArgument(categories, "categories");
        requireArgument(criteria, "criteria");
        requireArgument(order, "order");

        List<Appliance> found = new ArrayList<>();
        for (Category category : distinct(categories)) {
            found.addAll(read(category.getType(), criteria));
        }
        found.sort(order.thenComparing(BY_ID));
        return Collections.unmodifiableList(found);
    }

    @Override
    public StockReport stockReport(Collection<Category> categories, SearchCriteria<Appliance> criteria)
            throws ServiceException {
        requireArgument(categories, "categories");
        requireArgument(criteria, "criteria");

        Map<Category, Stock> byCategory = new LinkedHashMap<>();
        for (Category category : distinct(categories)) {
            Stock stock = Stock.EMPTY;
            for (Appliance appliance : read(category.getType(), criteria)) {
                stock = stock.plus(appliance);
            }
            byCategory.put(category, stock);
        }
        return new StockReport(byCategory);
    }

    private <A extends Appliance> List<A> read(Class<A> type, SearchCriteria<Appliance> criteria)
            throws ServiceException {
        try {
            return daos.get(type).find(criteria);
        } catch (DaoException e) {
            throw new ServiceException("Warehouse data is unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * @return the categories without repeats, in category order
     */
    private static Set<Category> distinct(Collection<Category> categories) {
        Set<Category> set = EnumSet.noneOf(Category.class);
        for (Category category : categories) {
            set.add(requireArgument(category, "category"));
        }
        return set;
    }

    private static <T> T requireArgument(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }
}
