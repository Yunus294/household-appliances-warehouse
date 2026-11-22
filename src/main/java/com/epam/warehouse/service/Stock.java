package com.epam.warehouse.service;

import com.epam.warehouse.entity.Appliance;

/**
 * Totals for a group of appliances.
 *
 * @param models how many different appliances
 * @param units  how many units of them are in stock
 * @param value  what those units are worth, in US dollars
 */
public record Stock(int models, long units, long value) {

    public static final Stock EMPTY = new Stock(0, 0, 0);

    public Stock plus(Appliance appliance) {
        return new Stock(models + 1, units + appliance.getQuantity(), value + appliance.getTotalCost());
    }

    public Stock plus(Stock other) {
        return new Stock(models + other.models, units + other.units, value + other.value);
    }
}
