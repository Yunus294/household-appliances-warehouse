package com.epam.warehouse.entity;

/**
 * An appliance that runs on mains power and has its rated power on the label.
 *
 * <p>Ovens, refrigerators and washing machines share this property, so a search like
 * "everything up to 2 kW" can treat them alike. Laptops run on a battery and don't implement it.
 */
public interface PowerConsumable {

    /**
     * @return rated power consumption, in watts
     */
    int getPowerConsumption();
}
