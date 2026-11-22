package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.entity.EnergyClass;
import com.epam.warehouse.entity.WashingMachine;

public final class WashingMachineMapper extends ApplianceMapper<WashingMachine, WashingMachine.Builder> {

    private static final String LOAD = "load";
    private static final String SPIN = "spin";

    public WashingMachineMapper() {
        super(WashingMachine.class);
    }

    @Override
    protected WashingMachine.Builder newBuilder() {
        return WashingMachine.builder();
    }

    @Override
    protected void readOwnColumns(DataRow row, WashingMachine.Builder builder) {
        builder.powerConsumption(row.intValue(POWER))
                .energyClass(row.enumValue(ENERGY, EnergyClass.class))
                .maxLoadKg(row.doubleValue(LOAD))
                .spinSpeedRpm(row.intValue(SPIN));
    }
}
