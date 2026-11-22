package com.epam.warehouse.entity;

import java.util.Optional;

/**
 * The kinds of appliances the warehouse stocks.
 */
public enum Category {

    LAPTOP("laptop", "Laptop", Laptop.class),
    OVEN("oven", "Oven", Oven.class),
    REFRIGERATOR("refrigerator", "Refrigerator", Refrigerator.class),
    WASHING_MACHINE("washing-machine", "Washing machine", WashingMachine.class);

    private final String key;
    private final String displayName;
    private final Class<? extends Appliance> type;

    Category(String key, String displayName, Class<? extends Appliance> type) {
        this.key = key;
        this.displayName = displayName;
        this.type = type;
    }

    /**
     * @return short machine name, used in data files and settings, e.g. "washing-machine"
     */
    public String getKey() {
        return key;
    }

    /**
     * @return name for people, e.g. "Washing machine"
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the entity class of this category
     */
    public Class<? extends Appliance> getType() {
        return type;
    }

    /**
     * @throws IllegalArgumentException if the class isn't one of the categories
     */
    public static Category of(Class<? extends Appliance> type) {
        for (Category category : values()) {
            if (category.type == type) {
                return category;
            }
        }
        throw new IllegalArgumentException("No category for " + type.getName());
    }

    /**
     * @return the category with this key, ignoring case
     */
    public static Optional<Category> byKey(String key) {
        for (Category category : values()) {
            if (category.key.equalsIgnoreCase(key)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }
}
