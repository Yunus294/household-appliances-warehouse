package com.epam.warehouse.criteria;

import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchCriteriaTest {

    private final Laptop laptop = Appliances.laptop().build();

    @Test
    void emptyCriteriaMatchEverything() {
        assertTrue(SearchCriteria.<Laptop>create().test(laptop));
    }

    @Test
    void everyParameterMustMatch() {
        SearchCriteria<Laptop> criteria = SearchCriteria.<Laptop>create()
                .add(new TextParameter<Appliance>("brand", Appliance::getBrand, "asus"))
                .add(new RangeParameter<Laptop, Integer>("ram", Laptop::getRamGb, Range.of(8, 16)));
        assertTrue(criteria.test(laptop));

        criteria.add(Parameter.none());
        assertFalse(criteria.test(laptop));
    }

    @Test
    void addReturnsTheSameCriteria() {
        SearchCriteria<Laptop> criteria = SearchCriteria.create();

        assertSame(criteria, criteria.add(Parameter.any()));
    }

    @Test
    void parameterIsRequired() {
        assertThrows(NullPointerException.class, () -> SearchCriteria.<Laptop>create().add(null));
    }
}
