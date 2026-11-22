package com.epam.warehouse.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * An appliance model stocked in the warehouse: what it is, what one unit costs and how many
 * units are on the shelves.
 *
 * <p>Objects are immutable and are created with the {@link Builder} of a concrete subclass,
 * e.g. {@code Laptop.builder().id(101).brand("ASUS")...build()}. The builder checks every value,
 * so an {@code Appliance} that exists is always valid.
 */
public abstract class Appliance implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final long id;
    private final String brand;
    private final String model;
    private final long price;
    private final int quantity;
    private final double weight;

    protected Appliance(Builder<?, ?> builder) {
        this.id = builder.id;
        this.brand = builder.brand;
        this.model = builder.model;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.weight = builder.weight;
    }

    /**
     * @return the category this kind of appliance belongs to
     */
    public abstract Category getCategory();

    /**
     * @return catalog number, unique across the whole warehouse
     */
    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    /**
     * @return brand and model together, e.g. "ASUS Vivobook 15"
     */
    public String getName() {
        return brand + " " + model;
    }

    /**
     * @return price of one unit, in US dollars
     */
    public long getPrice() {
        return price;
    }

    /**
     * @return units in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return weight of one unit, in kilograms
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return value of all units in stock: price × quantity, in US dollars
     */
    public long getTotalCost() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + commonFields() + ", " + ownFields() + "}";
    }

    private String commonFields() {
        return "id=" + id
                + ", brand=" + brand
                + ", model=" + model
                + ", price=" + price
                + ", quantity=" + quantity
                + ", weight=" + weight
                + ", totalCost=" + getTotalCost();
    }

    /**
     * @return the fields a subclass adds, for {@link #toString()}
     */
    protected abstract String ownFields();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appliance other = (Appliance) o;
        return id == other.id
                && price == other.price
                && quantity == other.quantity
                && Double.compare(weight, other.weight) == 0
                && brand.equals(other.brand)
                && model.equals(other.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, model, price, quantity, weight);
    }

    /**
     * Collects the fields shared by every appliance. {@code SELF} is the concrete builder type, so
     * the chained calls keep returning it: {@code Laptop.builder().price(649).cpu("Intel")} compiles
     * in any order.
     *
     * @param <SELF> the concrete builder
     * @param <T>    the appliance it builds
     */
    public abstract static class Builder<SELF extends Builder<SELF, T>, T extends Appliance> {

        private Long id;
        private String brand;
        private String model;
        private Long price;
        private Integer quantity;
        private Double weight;

        public SELF id(long id) {
            this.id = Require.positive(id, "id");
            return self();
        }

        public SELF brand(String brand) {
            this.brand = Require.notBlank(brand, "brand");
            return self();
        }

        public SELF model(String model) {
            this.model = Require.notBlank(model, "model");
            return self();
        }

        public SELF price(long price) {
            this.price = Require.positive(price, "price");
            return self();
        }

        public SELF quantity(int quantity) {
            this.quantity = Require.notNegative(quantity, "quantity");
            return self();
        }

        public SELF weight(double weight) {
            this.weight = Require.positive(weight, "weight");
            return self();
        }

        /**
         * @return the appliance
         * @throws IllegalStateException if a field was never set
         */
        public final T build() {
            Require.present(id, "id");
            Require.present(brand, "brand");
            Require.present(model, "model");
            Require.present(price, "price");
            Require.present(quantity, "quantity");
            Require.present(weight, "weight");
            return create();
        }

        /**
         * Checks the subclass fields and calls the subclass constructor.
         */
        protected abstract T create();

        @SuppressWarnings("unchecked")
        protected final SELF self() {
            return (SELF) this;
        }
    }
}
