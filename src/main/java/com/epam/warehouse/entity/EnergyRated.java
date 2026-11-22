package com.epam.warehouse.entity;

/**
 * An appliance sold with an EU energy-efficiency label (refrigerators, washing machines).
 */
public interface EnergyRated {

    EnergyClass getEnergyClass();
}
