package com.epam.warehouse.entity;

import java.io.Serial;
import java.util.Objects;

/**
 * A fridge-freezer.
 */
public final class Refrigerator extends Appliance implements PowerConsumable, EnergyRated {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int powerConsumption;
    private final EnergyClass energyClass;
    private final int capacityLiters;
    private final int freezerLiters;
    private final boolean noFrost;

    private Refrigerator(Builder builder) {
        super(builder);
        this.powerConsumption = builder.powerConsumption;
        this.energyClass = builder.energyClass;
        this.capacityLiters = builder.capacityLiters;
        this.freezerLiters = builder.freezerLiters;
        this.noFrost = builder.noFrost;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Category getCategory() {
        return Category.REFRIGERATOR;
    }

    @Override
    public int getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public EnergyClass getEnergyClass() {
        return energyClass;
    }

    /**
     * @return total volume, freezer included, in liters
     */
    public int getCapacityLiters() {
        return capacityLiters;
    }

    /**
     * @return freezer volume, in liters
     */
    public int getFreezerLiters() {
        return freezerLiters;
    }

    /**
     * @return whether the freezer defrosts itself
     */
    public boolean isNoFrost() {
        return noFrost;
    }

    @Override
    protected String ownFields() {
        return "powerConsumption=" + powerConsumption
                + ", energyClass=" + energyClass
                + ", capacityLiters=" + capacityLiters
                + ", freezerLiters=" + freezerLiters
                + ", noFrost=" + noFrost;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Refrigerator other = (Refrigerator) o;
        return powerConsumption == other.powerConsumption
                && energyClass == other.energyClass
                && capacityLiters == other.capacityLiters
                && freezerLiters == other.freezerLiters
                && noFrost == other.noFrost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), powerConsumption, energyClass, capacityLiters,
                freezerLiters, noFrost);
    }

    public static final class Builder extends Appliance.Builder<Builder, Refrigerator> {

        private Integer powerConsumption;
        private EnergyClass energyClass;
        private Integer capacityLiters;
        private Integer freezerLiters;
        private Boolean noFrost;

        private Builder() {
        }

        public Builder powerConsumption(int watts) {
            this.powerConsumption = Require.positive(watts, "power");
            return this;
        }

        public Builder energyClass(EnergyClass energyClass) {
            this.energyClass = Require.notNull(energyClass, "energy");
            return this;
        }

        public Builder capacityLiters(int liters) {
            this.capacityLiters = Require.positive(liters, "capacity");
            return this;
        }

        public Builder freezerLiters(int liters) {
            this.freezerLiters = Require.positive(liters, "freezer");
            return this;
        }

        public Builder noFrost(boolean noFrost) {
            this.noFrost = noFrost;
            return this;
        }

        @Override
        protected Refrigerator create() {
            Require.present(powerConsumption, "power");
            Require.present(energyClass, "energy");
            Require.present(capacityLiters, "capacity");
            Require.present(freezerLiters, "freezer");
            Require.present(noFrost, "nofrost");
            if (freezerLiters > capacityLiters) {
                throw new IllegalArgumentException("freezer (" + freezerLiters
                        + " l) can't be bigger than the whole capacity (" + capacityLiters + " l)");
            }
            return new Refrigerator(this);
        }
    }
}
