package com.epam.warehouse.entity;

import java.io.Serial;
import java.util.Objects;

/**
 * A laptop computer.
 */
public final class Laptop extends Appliance {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Operating system installed on the laptop.
     */
    public enum Os {
        WINDOWS, MACOS, LINUX, CHROME_OS
    }

    private final Os os;
    private final String cpu;
    private final int ramGb;
    private final int storageGb;
    private final double screenInches;
    private final int batteryWh;

    private Laptop(Builder builder) {
        super(builder);
        this.os = builder.os;
        this.cpu = builder.cpu;
        this.ramGb = builder.ramGb;
        this.storageGb = builder.storageGb;
        this.screenInches = builder.screenInches;
        this.batteryWh = builder.batteryWh;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Category getCategory() {
        return Category.LAPTOP;
    }

    public Os getOs() {
        return os;
    }

    public String getCpu() {
        return cpu;
    }

    /**
     * @return memory, in gigabytes
     */
    public int getRamGb() {
        return ramGb;
    }

    /**
     * @return disk size, in gigabytes
     */
    public int getStorageGb() {
        return storageGb;
    }

    /**
     * @return screen diagonal, in inches
     */
    public double getScreenInches() {
        return screenInches;
    }

    /**
     * @return battery capacity, in watt-hours
     */
    public int getBatteryWh() {
        return batteryWh;
    }

    @Override
    protected String ownFields() {
        return "os=" + os
                + ", cpu=" + cpu
                + ", ramGb=" + ramGb
                + ", storageGb=" + storageGb
                + ", screenInches=" + screenInches
                + ", batteryWh=" + batteryWh;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Laptop other = (Laptop) o;
        return os == other.os
                && cpu.equals(other.cpu)
                && ramGb == other.ramGb
                && storageGb == other.storageGb
                && Double.compare(screenInches, other.screenInches) == 0
                && batteryWh == other.batteryWh;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), os, cpu, ramGb, storageGb, screenInches, batteryWh);
    }

    public static final class Builder extends Appliance.Builder<Builder, Laptop> {

        private Os os;
        private String cpu;
        private Integer ramGb;
        private Integer storageGb;
        private Double screenInches;
        private Integer batteryWh;

        private Builder() {
        }

        public Builder os(Os os) {
            this.os = Require.notNull(os, "os");
            return this;
        }

        public Builder cpu(String cpu) {
            this.cpu = Require.notBlank(cpu, "cpu");
            return this;
        }

        public Builder ramGb(int ramGb) {
            this.ramGb = Require.positive(ramGb, "ram");
            return this;
        }

        public Builder storageGb(int storageGb) {
            this.storageGb = Require.positive(storageGb, "storage");
            return this;
        }

        public Builder screenInches(double screenInches) {
            this.screenInches = Require.positive(screenInches, "screen");
            return this;
        }

        public Builder batteryWh(int batteryWh) {
            this.batteryWh = Require.positive(batteryWh, "battery");
            return this;
        }

        @Override
        protected Laptop create() {
            Require.present(os, "os");
            Require.present(cpu, "cpu");
            Require.present(ramGb, "ram");
            Require.present(storageGb, "storage");
            Require.present(screenInches, "screen");
            Require.present(batteryWh, "battery");
            return new Laptop(this);
        }
    }
}
