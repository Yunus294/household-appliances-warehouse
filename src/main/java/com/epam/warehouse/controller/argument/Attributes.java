package com.epam.warehouse.controller.argument;

import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.entity.EnergyClass;
import com.epam.warehouse.entity.EnergyRated;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.entity.PowerConsumable;
import com.epam.warehouse.entity.Refrigerator;
import com.epam.warehouse.entity.WashingMachine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Every property the user can search and sort by. The order here is the order of the help and of
 * the details column.
 */
public final class Attributes {

    private static final String WHOLE_NUMBER = "a whole number";
    private static final String NUMBER = "a number";

    private static final List<Attribute<?, ?>> ALL = List.of(
            // every appliance
            longs("id", "catalog number", Appliance.class, Appliance::getId),
            new TextAttribute<>("name", "brand and model, any part of it", Appliance.class, Appliance::getName),
            new TextAttribute<>("brand", "manufacturer", Appliance.class, Appliance::getBrand),
            longs("price", "price of one unit, $", Appliance.class, Appliance::getPrice),
            ints("quantity", "units in stock", Appliance.class, Appliance::getQuantity),
            longs("total", "stock value = price x quantity, $", Appliance.class, Appliance::getTotalCost),
            decimals("weight", "weight of one unit, kg", Appliance.class, Appliance::getWeight),
            // groups of categories
            ints("power", "power consumption, W", PowerConsumable.class, PowerConsumable::getPowerConsumption),
            new EnumAttribute<>("energy", "EU energy class", EnergyRated.class, EnergyRated::getEnergyClass,
                    EnergyClass.class),
            // laptops
            new EnumAttribute<>("os", "operating system", Laptop.class, Laptop::getOs, Laptop.Os.class),
            new TextAttribute<>("cpu", "processor, any part of its name", Laptop.class, Laptop::getCpu),
            ints("ram", "memory, GB", Laptop.class, Laptop::getRamGb),
            ints("storage", "disk, GB", Laptop.class, Laptop::getStorageGb),
            decimals("screen", "screen diagonal, inches", Laptop.class, Laptop::getScreenInches),
            ints("battery", "battery capacity, Wh", Laptop.class, Laptop::getBatteryWh),
            // ovens
            ints("volume", "oven volume, l", Oven.class, Oven::getVolumeLiters),
            ints("temperature", "highest temperature, C", Oven.class, Oven::getMaxTemperature),
            // refrigerators
            ints("capacity", "total volume, l", Refrigerator.class, Refrigerator::getCapacityLiters),
            ints("freezer", "freezer volume, l", Refrigerator.class, Refrigerator::getFreezerLiters),
            new BooleanAttribute<>("nofrost", "frost-free freezer", Refrigerator.class, Refrigerator::isNoFrost),
            // washing machines
            decimals("load", "largest load of laundry, kg", WashingMachine.class, WashingMachine::getMaxLoadKg),
            ints("spin", "fastest spin, rpm", WashingMachine.class, WashingMachine::getSpinSpeedRpm));

    private static final Map<String, Attribute<?, ?>> BY_KEY = index(ALL);

    private Attributes() {
    }

    public static List<Attribute<?, ?>> all() {
        return ALL;
    }

    /**
     * @param key ignoring case
     */
    public static Optional<Attribute<?, ?>> byKey(String key) {
        return Optional.ofNullable(BY_KEY.get(key.strip().toLowerCase(Locale.ROOT)));
    }

    private static <T> NumberAttribute<T, Long> longs(String key, String description, Class<T> owner,
                                                      Function<? super T, Long> getter) {
        return new NumberAttribute<>(key, description, owner, getter, Long::valueOf, WHOLE_NUMBER);
    }

    private static <T> NumberAttribute<T, Integer> ints(String key, String description, Class<T> owner,
                                                        Function<? super T, Integer> getter) {
        return new NumberAttribute<>(key, description, owner, getter, Integer::valueOf, WHOLE_NUMBER);
    }

    private static <T> NumberAttribute<T, Double> decimals(String key, String description, Class<T> owner,
                                                           Function<? super T, Double> getter) {
        return new NumberAttribute<>(key, description, owner, getter, Double::valueOf, NUMBER);
    }

    private static Map<String, Attribute<?, ?>> index(List<Attribute<?, ?>> attributes) {
        Map<String, Attribute<?, ?>> byKey = new LinkedHashMap<>();
        for (Attribute<?, ?> attribute : attributes) {
            if (byKey.putIfAbsent(attribute.key(), attribute) != null) {
                throw new IllegalStateException("Two attributes have the key '" + attribute.key() + "'");
            }
        }
        return Collections.unmodifiableMap(byKey);
    }
}
