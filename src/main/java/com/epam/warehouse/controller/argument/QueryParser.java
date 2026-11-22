package com.epam.warehouse.controller.argument;

import com.epam.warehouse.controller.Request;
import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Checks a request and turns it into a {@link Query}:
 * {@code find laptops ram=16 price=500;900 sort=price order=desc}.
 *
 * <ul>
 *     <li>exactly one target: a category or {@code all};</li>
 *     <li>every other {@code key=value} is a filter on an {@link Attribute} that the target has;</li>
 *     <li>{@code sort=<key>} and {@code order=asc|desc} set the order (by id when absent).</li>
 * </ul>
 */
public final class QueryParser {

    public static final String SORT = "sort";
    public static final String ORDER = "order";

    private static final String ASCENDING = "asc";
    private static final String DESCENDING = "desc";
    private static final Comparator<Appliance> BY_ID = Comparator.comparingLong(Appliance::getId);

    /**
     * @throws InvalidRequestException with a message for the user if the request is wrong
     */
    public Query parse(Request request, QuerySyntax syntax) throws InvalidRequestException {
        Set<Category> categories = Targets.resolve(target(request));

        Map<String, String> filters = new LinkedHashMap<>(request.options());
        String sortKey = filters.remove(SORT);
        String direction = filters.remove(ORDER);

        if (!filters.isEmpty() && !syntax.allowsFilters()) {
            throw new InvalidRequestException("'" + request.command() + "' takes no filters ("
                    + String.join(", ", filters.keySet()) + "). Use 'find' to filter");
        }
        if ((sortKey != null || direction != null) && !syntax.allowsSorting()) {
            throw new InvalidRequestException("'" + request.command() + "' doesn't sort. Use 'find' or 'list' to sort");
        }
        return new Query(categories, criteria(filters, categories), order(sortKey, direction, categories));
    }

    private static String target(Request request) throws InvalidRequestException {
        List<String> arguments = request.arguments();
        if (arguments.isEmpty()) {
            throw new InvalidRequestException("Say where to look: " + Targets.choices()
                    + ". For example: " + request.command() + " laptops");
        }
        if (arguments.size() > 1) {
            throw new InvalidRequestException("Unexpected '" + arguments.get(1)
                    + "'. Parameters are written as key=value, e.g. price=500;900");
        }
        return arguments.get(0);
    }

    private static SearchCriteria<Appliance> criteria(Map<String, String> filters, Set<Category> categories)
            throws InvalidRequestException {
        SearchCriteria<Appliance> criteria = SearchCriteria.create();
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            criteria.add(attribute(filter.getKey(), categories).parameter(filter.getValue()));
        }
        return criteria;
    }

    private static Comparator<Appliance> order(String sortKey, String direction, Set<Category> categories)
            throws InvalidRequestException {
        boolean descending = isDescending(direction);
        if (sortKey == null) {
            return descending ? BY_ID.reversed() : BY_ID;
        }
        return attribute(sortKey, categories).comparator(descending);
    }

    private static boolean isDescending(String direction) throws InvalidRequestException {
        if (direction == null || ASCENDING.equalsIgnoreCase(direction.strip())) {
            return false;
        }
        if (DESCENDING.equalsIgnoreCase(direction.strip())) {
            return true;
        }
        throw new InvalidRequestException("'" + ORDER + "' must be " + ASCENDING + " or " + DESCENDING
                + ", but was '" + direction + "'");
    }

    private static Attribute<?, ?> attribute(String key, Set<Category> categories) throws InvalidRequestException {
        Attribute<?, ?> attribute = Attributes.byKey(key).orElseThrow(() -> new InvalidRequestException(
                "Unknown parameter '" + key + "'. Type 'help' to see the parameters"));
        if (categories.stream().noneMatch(attribute::appliesTo)) {
            throw new InvalidRequestException("'" + key + "' doesn't apply to " + Targets.describe(categories));
        }
        return attribute;
    }
}
