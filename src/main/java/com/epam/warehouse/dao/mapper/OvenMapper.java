package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.entity.Oven;

public final class OvenMapper extends ApplianceMapper<Oven, Oven.Builder> {

    private static final String VOLUME = "volume";
    private static final String TEMPERATURE = "temperature";

    public OvenMapper() {
        super(Oven.class);
    }

    @Override
    protected Oven.Builder newBuilder() {
        return Oven.builder();
    }

    @Override
    protected void readOwnColumns(DataRow row, Oven.Builder builder) {
        builder.powerConsumption(row.intValue(POWER))
                .volumeLiters(row.intValue(VOLUME))
                .maxTemperature(row.intValue(TEMPERATURE));
    }
}
