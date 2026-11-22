package com.epam.warehouse.criteria;

import com.epam.warehouse.entity.Appliance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Criteria that require every condition to hold (logical AND).
 */
final class AllOfCriteria<A extends Appliance> implements SearchCriteria<A> {

    private final List<Parameter<? super A>> parameters = new ArrayList<>();

    @Override
    public SearchCriteria<A> add(Parameter<? super A> parameter) {
        parameters.add(Objects.requireNonNull(parameter, "parameter"));
        return this;
    }

    @Override
    public boolean test(A appliance) {
        for (Parameter<? super A> parameter : parameters) {
            if (!parameter.test(appliance)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return parameters.isEmpty() ? "all" : parameters.toString();
    }
}
