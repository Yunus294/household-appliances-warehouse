package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.entity.Laptop;

public final class LaptopMapper extends ApplianceMapper<Laptop, Laptop.Builder> {

    private static final String OS = "os";
    private static final String CPU = "cpu";
    private static final String RAM = "ram";
    private static final String STORAGE = "storage";
    private static final String SCREEN = "screen";
    private static final String BATTERY = "battery";

    public LaptopMapper() {
        super(Laptop.class);
    }

    @Override
    protected Laptop.Builder newBuilder() {
        return Laptop.builder();
    }

    @Override
    protected void readOwnColumns(DataRow row, Laptop.Builder builder) {
        builder.os(row.enumValue(OS, Laptop.Os.class))
                .cpu(row.text(CPU))
                .ramGb(row.intValue(RAM))
                .storageGb(row.intValue(STORAGE))
                .screenInches(row.doubleValue(SCREEN))
                .batteryWh(row.intValue(BATTERY));
    }
}
