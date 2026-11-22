package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.entity.Appliance;

import java.util.Objects;

/**
 * Reads the columns every appliance has, then lets the subclass read its own ones (template method).
 *
 * @param <A> the appliance type
 * @param <B> its builder
 */
public abstract class ApplianceMapper<A extends Appliance, B extends Appliance.Builder<B, A>>
        implements RecordMapper<A> {

    protected static final String ID = "id";
    protected static final String BRAND = "brand";
    protected static final String MODEL = "model";
    protected static final String PRICE = "price";
    protected static final String QUANTITY = "quantity";
    protected static final String WEIGHT = "weight";
    /**
     * Column of every {@code PowerConsumable} appliance.
     */
    protected static final String POWER = "power";
    /**
     * Column of every {@code EnergyRated} appliance.
     */
    protected static final String ENERGY = "energy";

    private final Class<A> type;

    protected ApplianceMapper(Class<A> type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    @Override
    public final Class<A> type() {
        return type;
    }

    @Override
    public final A map(DataRow row) {
        B builder = newBuilder()
                .id(row.longValue(ID))
                .brand(row.text(BRAND))
                .model(row.text(MODEL))
                .price(row.longValue(PRICE))
                .quantity(row.intValue(QUANTITY))
                .weight(row.doubleValue(WEIGHT));
        readOwnColumns(row, builder);
        return builder.build();
    }

    protected abstract B newBuilder();

    protected abstract void readOwnColumns(DataRow row, B builder);
}
