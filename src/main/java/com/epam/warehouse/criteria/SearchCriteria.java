package com.epam.warehouse.criteria;

import com.epam.warehouse.entity.Appliance;

/**
 * A set of search conditions. An appliance matches when it meets all of them; criteria without
 * conditions match everything.
 *
 * <p>Criteria are plain data passed down the layers, like entities: the controller fills them from
 * the user's request, the service hands them to the DAO, and the DAO checks each appliance.
 *
 * @param <A> the appliances the criteria are checked against
 */
public interface SearchCriteria<A extends Appliance> {

    /**
     * Adds a condition.
     *
     * @return these criteria, to chain calls
     */
    SearchCriteria<A> add(Parameter<? super A> parameter);

    /**
     * @return {@code true} if the appliance meets every condition
     */
    boolean test(A appliance);

    /**
     * @return empty criteria, which match every appliance until conditions are added
     */
    static <A extends Appliance> SearchCriteria<A> create() {
        return new AllOfCriteria<>();
    }
}
