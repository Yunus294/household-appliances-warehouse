package com.epam.warehouse.controller.argument;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;

import java.util.Comparator;
import java.util.Set;

/**
 * A request turned into Java objects the service understands.
 *
 * @param categories where to look
 * @param criteria   what to look for
 * @param order      how to sort
 */
public record Query(Set<Category> categories, SearchCriteria<Appliance> criteria, Comparator<Appliance> order) {
}
