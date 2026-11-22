package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.entity.EnergyClass;
import com.epam.warehouse.entity.Refrigerator;

public final class RefrigeratorMapper extends ApplianceMapper<Refrigerator, Refrigerator.Builder> {

    private static final String CAPACITY = "capacity";
    private static final String FREEZER = "freezer";
    private static final String NO_FROST = "nofrost";

    public RefrigeratorMapper() {
        super(Refrigerator.class);
    }

    @Override
    protected Refrigerator.Builder newBuilder() {
        return Refrigerator.builder();
    }

    @Override
    protected void readOwnColumns(DataRow row, Refrigerator.Builder builder) {
        builder.powerConsumption(row.intValue(POWER))
                .energyClass(row.enumValue(ENERGY, EnergyClass.class))
                .capacityLiters(row.intValue(CAPACITY))
                .freezerLiters(row.intValue(FREEZER))
                .noFrost(row.booleanValue(NO_FROST));
    }
}
