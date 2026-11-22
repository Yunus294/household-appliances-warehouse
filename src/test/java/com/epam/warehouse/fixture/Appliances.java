package com.epam.warehouse.fixture;

import com.epam.warehouse.entity.EnergyClass;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.entity.Refrigerator;
import com.epam.warehouse.entity.WashingMachine;

/**
 * Builders filled with valid sample values; a test changes only what it is about.
 */
public final class Appliances {

    private Appliances() {
    }

    public static Laptop.Builder laptop() {
        return Laptop.builder()
                .id(101).brand("ASUS").model("Vivobook 15").price(649).quantity(14).weight(1.7)
                .os(Laptop.Os.WINDOWS).cpu("Intel Core i5-1235U").ramGb(16).storageGb(512)
                .screenInches(15.6).batteryWh(42);
    }

    public static Oven.Builder oven() {
        return Oven.builder()
                .id(201).brand("Bosch").model("Serie 4").price(549).quantity(8).weight(34)
                .powerConsumption(3400).volumeLiters(71).maxTemperature(275);
    }

    public static Refrigerator.Builder refrigerator() {
        return Refrigerator.builder()
                .id(301).brand("Samsung").model("RB38T675ESA").price(899).quantity(7).weight(70)
                .powerConsumption(120).energyClass(EnergyClass.C).capacityLiters(385).freezerLiters(114)
                .noFrost(true);
    }

    public static WashingMachine.Builder washingMachine() {
        return WashingMachine.builder()
                .id(401).brand("Bosch").model("Serie 6").price(599).quantity(8).weight(72)
                .powerConsumption(2300).energyClass(EnergyClass.A).maxLoadKg(9).spinSpeedRpm(1400);
    }
}
