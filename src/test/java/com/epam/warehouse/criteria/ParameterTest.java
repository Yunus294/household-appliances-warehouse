package com.epam.warehouse.criteria;

import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.entity.PowerConsumable;
import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterTest {

    private final Laptop laptop = Appliances.laptop().build();
    private final Oven oven = Appliances.oven().build();

    @Test
    void anyMatchesEverythingAndNoneMatchesNothing() {
        assertTrue(Parameter.<Appliance>any().test(laptop));
        assertFalse(Parameter.<Appliance>none().test(laptop));
    }

    @Test
    void rangeParameterChecksTheValue() {
        Parameter<Appliance> price = new RangeParameter<>("price", Appliance::getPrice, Range.of(600L, 700L));

        assertTrue(price.test(laptop));
        assertFalse(price.test(oven));
        assertEquals("price in [600, 700]", price.toString());
    }

    @Test
    void textParameterMatchesAnyPartIgnoringCase() {
        assertTrue(new TextParameter<Laptop>("cpu", Laptop::getCpu, "core I5").test(laptop));
        assertFalse(new TextParameter<Laptop>("cpu", Laptop::getCpu, "ryzen").test(laptop));
    }

    @Test
    void textParameterNeedsText() {
        assertThrows(IllegalArgumentException.class, () -> new TextParameter<Laptop>("cpu", Laptop::getCpu, " "));
    }

    @Test
    void equalsParameterComparesValues() {
        assertTrue(new EqualsParameter<Laptop, Laptop.Os>("os", Laptop::getOs, Laptop.Os.WINDOWS).test(laptop));
        assertFalse(new EqualsParameter<Laptop, Laptop.Os>("os", Laptop::getOs, Laptop.Os.LINUX).test(laptop));
    }

    @Test
    void onlyForSkipsItemsOfOtherTypes() {
        Parameter<Object> windows = Parameter.onlyFor(Laptop.class,
                new EqualsParameter<Laptop, Laptop.Os>("os", Laptop::getOs, Laptop.Os.WINDOWS));

        assertTrue(windows.test(laptop));
        assertFalse(windows.test(oven));
    }

    @Test
    void onlyForWorksWithInterfaces() {
        Parameter<Object> power = Parameter.onlyFor(PowerConsumable.class,
                new RangeParameter<>("power", PowerConsumable::getPowerConsumption, Range.of(3000, 3500)));

        assertTrue(power.test(oven));
        assertFalse(power.test(laptop));
    }
}
