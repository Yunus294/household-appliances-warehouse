package com.epam.warehouse.entity;

import java.io.Serial;
import java.util.Objects;

/**
 * A front-loading washing machine.
 */
public final class WashingMachine extends Appliance implements PowerConsumable, EnergyRated {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int powerConsumption;
    private final EnergyClass energyClass;
    private final double maxLoadKg;
    private final int spinSpeedRpm;

    private WashingMachine(Builder builder) {
        super(builder);
        this.powerConsumption = builder.powerConsumption;
        this.energyClass = builder.energyClass;
        this.maxLoadKg = builder.maxLoadKg;
        this.spinSpeedRpm = builder.spinSpeedRpm;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Category getCategory() {
        return Category.WASHING_MACHINE;
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
     * @return largest load of dry laundry, in kilograms
     */
    public double getMaxLoadKg() {
        return maxLoadKg;
    }

    /**
     * @return fastest spin, in revolutions per minute
     */
    public int getSpinSpeedRpm() {
        return spinSpeedRpm;
    }

    @Override
    protected String ownFields() {
        return "powerConsumption=" + powerConsumption
                + ", energyClass=" + energyClass
                + ", maxLoadKg=" + maxLoadKg
                + ", spinSpeedRpm=" + spinSpeedRpm;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        WashingMachine other = (WashingMachine) o;
        return powerConsumption == other.powerConsumption
                && energyClass == other.energyClass
                && Double.compare(maxLoadKg, other.maxLoadKg) == 0
                && spinSpeedRpm == other.spinSpeedRpm;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), powerConsumption, energyClass, maxLoadKg, spinSpeedRpm);
    }

    public static final class Builder extends Appliance.Builder<Builder, WashingMachine> {

        private Integer powerConsumption;
        private EnergyClass energyClass;
        private Double maxLoadKg;
        private Integer spinSpeedRpm;

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

        public Builder maxLoadKg(double kilograms) {
            this.maxLoadKg = Require.positive(kilograms, "load");
            return this;
        }

        public Builder spinSpeedRpm(int rpm) {
            this.spinSpeedRpm = Require.positive(rpm, "spin");
            return this;
        }

        @Override
        protected WashingMachine create() {
            Require.present(powerConsumption, "power");
            Require.present(energyClass, "energy");
            Require.present(maxLoadKg, "load");
            Require.present(spinSpeedRpm, "spin");
            return new WashingMachine(this);
        }
    }
}
