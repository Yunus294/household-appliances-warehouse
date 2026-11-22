package com.epam.warehouse.entity;

import java.io.Serial;
import java.util.Objects;

/**
 * A built-in electric oven.
 */
public final class Oven extends Appliance implements PowerConsumable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int powerConsumption;
    private final int volumeLiters;
    private final int maxTemperature;

    private Oven(Builder builder) {
        super(builder);
        this.powerConsumption = builder.powerConsumption;
        this.volumeLiters = builder.volumeLiters;
        this.maxTemperature = builder.maxTemperature;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Category getCategory() {
        return Category.OVEN;
    }

    @Override
    public int getPowerConsumption() {
        return powerConsumption;
    }

    /**
     * @return cavity volume, in liters
     */
    public int getVolumeLiters() {
        return volumeLiters;
    }

    /**
     * @return highest temperature, in °C
     */
    public int getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    protected String ownFields() {
        return "powerConsumption=" + powerConsumption
                + ", volumeLiters=" + volumeLiters
                + ", maxTemperature=" + maxTemperature;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Oven other = (Oven) o;
        return powerConsumption == other.powerConsumption
                && volumeLiters == other.volumeLiters
                && maxTemperature == other.maxTemperature;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), powerConsumption, volumeLiters, maxTemperature);
    }

    public static final class Builder extends Appliance.Builder<Builder, Oven> {

        private Integer powerConsumption;
        private Integer volumeLiters;
        private Integer maxTemperature;

        private Builder() {
        }

        public Builder powerConsumption(int watts) {
            this.powerConsumption = Require.positive(watts, "power");
            return this;
        }

        public Builder volumeLiters(int liters) {
            this.volumeLiters = Require.positive(liters, "volume");
            return this;
        }

        public Builder maxTemperature(int celsius) {
            this.maxTemperature = Require.positive(celsius, "temperature");
            return this;
        }

        @Override
        protected Oven create() {
            Require.present(powerConsumption, "power");
            Require.present(volumeLiters, "volume");
            Require.present(maxTemperature, "temperature");
            return new Oven(this);
        }
    }
}
