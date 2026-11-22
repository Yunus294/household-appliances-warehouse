package com.epam.warehouse.controller.argument;

import com.epam.warehouse.criteria.Parameter;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.entity.Refrigerator;
import com.epam.warehouse.entity.WashingMachine;
import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributesTest {

    private final Laptop laptop = Appliances.laptop().build();
    private final Oven oven = Appliances.oven().build();
    private final Refrigerator refrigerator = Appliances.refrigerator().build();
    private final WashingMachine washingMachine = Appliances.washingMachine().build();

    private static Attribute<?, ?> attribute(String key) {
        return Attributes.byKey(key).orElseThrow();
    }

    private static Parameter<Object> parameter(String key, String value) throws InvalidRequestException {
        return attribute(key).parameter(value);
    }

    private static String errorOf(String key, String value) {
        return assertThrows(InvalidRequestException.class, () -> parameter(key, value)).getMessage();
    }

    @Test
    void keysAreFoundIgnoringCase() {
        assertEquals("price", attribute(" Price ").key());
        assertEquals(Optional.empty(), Attributes.byKey("color"));
    }

    @Test
    void attributeKnowsWhichCategoriesHaveIt() {
        assertTrue(attribute("price").appliesTo(Category.OVEN));
        assertTrue(attribute("power").appliesTo(Category.WASHING_MACHINE));
        assertFalse(attribute("power").appliesTo(Category.LAPTOP));
        assertTrue(attribute("energy").appliesTo(Category.REFRIGERATOR));
        assertFalse(attribute("energy").appliesTo(Category.OVEN));
        assertFalse(attribute("os").appliesTo(Category.OVEN));
    }

    @Test
    void numberTakesOneValueOrARange() throws InvalidRequestException {
        assertTrue(parameter("ram", "16").test(laptop));
        assertFalse(parameter("ram", "8").test(laptop));
        assertTrue(parameter("price", "600;700").test(laptop));
        assertTrue(parameter("price", " 649 ; 649 ").test(laptop));
        assertTrue(parameter("screen", "15.6").test(laptop));
    }

    @Test
    void numberParameterSkipsAppliancesWithoutIt() throws InvalidRequestException {
        assertFalse(parameter("ram", "0;1000").test(oven));
        assertTrue(parameter("power", "3000;4000").test(oven));
        assertFalse(parameter("power", "0;100000").test(laptop));
    }

    @Test
    void wrongNumbersAreExplained() {
        assertEquals("'price' must be a whole number, but was 'abc'", errorOf("price", "abc"));
        assertEquals("'ram' must be a whole number, but was '16.5'", errorOf("ram", "16.5"));
        assertEquals("'weight' must be a number, but was 'NaN'", errorOf("weight", "NaN"));
        assertEquals("'price' can't be negative, but was -5", errorOf("price", "-5"));
        assertEquals("'price' range start 900 is greater than its end 100", errorOf("price", "900;100"));
        assertEquals("'price' needs a value on both sides of ';', e.g. 500;900", errorOf("price", ";900"));
        assertEquals("'price' takes one value or a range from;to, e.g. 500;900", errorOf("price", "1;2;3"));
    }

    @Test
    void textMatchesAnyPartIgnoringCase() throws InvalidRequestException {
        assertTrue(parameter("name", "vivo").test(laptop));
        assertTrue(parameter("cpu", "I5-1235").test(laptop));
        assertFalse(parameter("brand", "samsung").test(laptop));
    }

    @Test
    void enumAcceptsAnyCaseAndDashes() throws InvalidRequestException {
        assertTrue(parameter("os", "Windows").test(laptop));
        assertFalse(parameter("os", "chrome-os").test(laptop));
        assertTrue(parameter("energy", "c").test(refrigerator));
        assertTrue(parameter("energy", "A").test(washingMachine));
        assertEquals("'os' has no value 'solaris', use one of: windows, macos, linux, chrome-os",
                errorOf("os", "solaris"));
    }

    @Test
    void booleanIsYesOrNo() throws InvalidRequestException {
        assertTrue(parameter("nofrost", "yes").test(refrigerator));
        assertFalse(parameter("nofrost", "no").test(refrigerator));
        assertEquals("'nofrost' must be yes or no, but was 'maybe'", errorOf("nofrost", "maybe"));
    }

    @Test
    void valuesArePrintedTheWayTheyAreTyped() {
        assertEquals(Optional.of("\"Intel Core i5-1235U\""), attribute("cpu").format(laptop));
        assertEquals(Optional.of("windows"), attribute("os").format(laptop));
        assertEquals(Optional.of("9"), attribute("load").format(washingMachine));
        assertEquals(Optional.of("yes"), attribute("nofrost").format(refrigerator));
        assertEquals(Optional.empty(), attribute("ram").format(oven));
    }

    @Test
    void appliancesWithoutTheKeySortLastBothWays() {
        Laptop small = Appliances.laptop().id(1).ramGb(8).build();
        Laptop big = Appliances.laptop().id(2).ramGb(32).build();
        List<Appliance> mixed = List.of(oven, big, small);

        assertEquals(List.of(small, big, oven), mixed.stream().sorted(attribute("ram").comparator(false)).toList());
        assertEquals(List.of(big, small, oven), mixed.stream().sorted(attribute("ram").comparator(true)).toList());
    }

    @Test
    void helpShowsTheAllowedValues() {
        assertEquals("EU energy class: a, b, c, d, e, f, g", attribute("energy").help());
        assertEquals("frost-free freezer: yes or no", attribute("nofrost").help());
        assertEquals("price of one unit, $", attribute("price").help());
    }
}
