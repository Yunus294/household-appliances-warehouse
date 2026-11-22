package com.epam.warehouse.entity;

import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplianceTest {

    @Test
    void builderSetsEveryField() {
        Laptop laptop = Appliances.laptop().build();

        assertAll(
                () -> assertEquals(101, laptop.getId()),
                () -> assertEquals("ASUS", laptop.getBrand()),
                () -> assertEquals("Vivobook 15", laptop.getModel()),
                () -> assertEquals("ASUS Vivobook 15", laptop.getName()),
                () -> assertEquals(649, laptop.getPrice()),
                () -> assertEquals(14, laptop.getQuantity()),
                () -> assertEquals(1.7, laptop.getWeight()),
                () -> assertEquals(Laptop.Os.WINDOWS, laptop.getOs()),
                () -> assertEquals("Intel Core i5-1235U", laptop.getCpu()),
                () -> assertEquals(16, laptop.getRamGb()),
                () -> assertEquals(512, laptop.getStorageGb()),
                () -> assertEquals(15.6, laptop.getScreenInches()),
                () -> assertEquals(42, laptop.getBatteryWh()),
                () -> assertEquals(Category.LAPTOP, laptop.getCategory()));
    }

    @Test
    void totalCostIsPriceTimesQuantity() {
        assertEquals(9086, Appliances.laptop().price(649).quantity(14).build().getTotalCost());
    }

    @Test
    void totalCostOfEmptyStockIsZero() {
        assertEquals(0, Appliances.laptop().quantity(0).build().getTotalCost());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -649})
    void priceMustBePositive(long price) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Appliances.laptop().price(price));
        assertEquals("price must be positive, but was " + price, e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.5, Double.NaN, Double.POSITIVE_INFINITY})
    void weightMustBeAPositiveNumber(double weight) {
        assertThrows(IllegalArgumentException.class, () -> Appliances.laptop().weight(weight));
    }

    @Test
    void quantityCanBeZeroButNotNegative() {
        assertEquals(0, Appliances.laptop().quantity(0).build().getQuantity());
        assertThrows(IllegalArgumentException.class, () -> Appliances.laptop().quantity(-1));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void brandMustNotBeEmpty(String brand) {
        assertThrows(IllegalArgumentException.class, () -> Appliances.laptop().brand(brand));
    }

    @Test
    void textIsTrimmed() {
        assertEquals("ASUS", Appliances.laptop().brand("  ASUS ").build().getBrand());
    }

    @Test
    void missingOwnFieldIsNamed() {
        Laptop.Builder builder = Laptop.builder()
                .id(1).brand("ASUS").model("Vivobook").price(649).quantity(1).weight(1.7);

        IllegalStateException e = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("os is required", e.getMessage());
    }

    @Test
    void missingCommonFieldIsNamed() {
        Laptop.Builder builder = Laptop.builder().id(1).brand("ASUS").model("Vivobook").quantity(1).weight(1.7);

        IllegalStateException e = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("price is required", e.getMessage());
    }

    @Test
    void builderMethodsChainInAnyOrder() {
        Laptop laptop = Laptop.builder()
                .cpu("Apple M2").price(999).os(Laptop.Os.MACOS).id(105).ramGb(8).brand("Apple")
                .storageGb(256).model("MacBook Air").screenInches(13.6).weight(1.24).batteryWh(52).quantity(11)
                .build();

        assertEquals("Apple MacBook Air", laptop.getName());
    }

    @Test
    void equalAppliancesHaveEqualHashCodes() {
        Laptop first = Appliances.laptop().build();
        Laptop second = Appliances.laptop().build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void anyDifferentFieldMakesAppliancesDifferent() {
        Laptop laptop = Appliances.laptop().build();

        assertNotEquals(laptop, Appliances.laptop().id(102).build());
        assertNotEquals(laptop, Appliances.laptop().price(650).build());
        assertNotEquals(laptop, Appliances.laptop().ramGb(8).build());
        assertNotEquals(laptop, Appliances.laptop().os(Laptop.Os.LINUX).build());
    }

    @Test
    void appliancesOfDifferentCategoriesAreNeverEqual() {
        Laptop laptop = Appliances.laptop().build();
        Oven oven = Appliances.oven()
                .id(101).brand("ASUS").model("Vivobook 15").price(649).quantity(14).weight(1.7).build();

        assertNotEquals(laptop, oven);
        assertNotEquals(oven, laptop);
    }

    @Test
    void toStringShowsEveryField() {
        assertEquals("Laptop{id=101, brand=ASUS, model=Vivobook 15, price=649, quantity=14, weight=1.7, "
                        + "totalCost=9086, os=WINDOWS, cpu=Intel Core i5-1235U, ramGb=16, storageGb=512, "
                        + "screenInches=15.6, batteryWh=42}",
                Appliances.laptop().build().toString());
    }
}
