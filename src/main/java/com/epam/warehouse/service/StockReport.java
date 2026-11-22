package com.epam.warehouse.service;

import com.epam.warehouse.entity.Category;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stock totals of each category, in category order.
 */
public record StockReport(Map<Category, Stock> byCategory) {

    public StockReport {
        byCategory = Collections.unmodifiableMap(new LinkedHashMap<>(byCategory));
    }

    /**
     * @return totals over all the categories of the report
     */
    public Stock total() {
        Stock total = Stock.EMPTY;
        for (Stock stock : byCategory.values()) {
            total = total.plus(stock);
        }
        return total;
    }
}
