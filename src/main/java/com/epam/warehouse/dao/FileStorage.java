package com.epam.warehouse.dao;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.dao.format.RecordFormat;
import com.epam.warehouse.dao.mapper.LaptopMapper;
import com.epam.warehouse.dao.mapper.OvenMapper;
import com.epam.warehouse.dao.mapper.RecordMapper;
import com.epam.warehouse.dao.mapper.RefrigeratorMapper;
import com.epam.warehouse.dao.mapper.WashingMachineMapper;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Builds the DAOs for appliances kept in files.
 *
 * <p>Two layouts are supported, and they can be mixed:
 * <ul>
 *     <li>a file per category: {@code file(Category.LAPTOP, "laptops.csv")};</li>
 *     <li>one shared file with a {@code category} column: {@code sharedFile("warehouse.txt")}.</li>
 * </ul>
 * A category with its own file reads that file; the others read their rows of the shared file.
 */
public final class FileStorage {

    /**
     * Column of the shared file that says which category a row belongs to.
     */
    private static final String CATEGORY_COLUMN = "category";

    private static final List<RecordMapper<? extends Appliance>> MAPPERS = List.of(
            new LaptopMapper(),
            new OvenMapper(),
            new RefrigeratorMapper(),
            new WashingMachineMapper());

    private final RecordFormat format;
    private final Map<Category, String> ownFiles = new EnumMap<>(Category.class);
    private String sharedFile;

    public FileStorage(RecordFormat format) {
        this.format = Objects.requireNonNull(format, "format");
    }

    public FileStorage file(Category category, String location) {
        ownFiles.put(Objects.requireNonNull(category, "category"), requireLocation(location));
        return this;
    }

    public FileStorage sharedFile(String location) {
        this.sharedFile = requireLocation(location);
        return this;
    }

    /**
     * @return a DAO for every category
     * @throws IllegalStateException if a category has neither its own file nor a shared one
     */
    public DaoRegistry build() {
        DaoRegistry registry = new DaoRegistry();
        for (RecordMapper<? extends Appliance> mapper : MAPPERS) {
            register(registry, mapper);
        }
        return registry;
    }

    private <A extends Appliance> void register(DaoRegistry registry, RecordMapper<A> mapper) {
        registry.register(mapper.type(), new ApplianceDaoImpl<>(source(mapper)));
    }

    private <A extends Appliance> Source<A> source(RecordMapper<A> mapper) {
        Category category = Category.of(mapper.type());
        String ownFile = ownFiles.get(category);
        if (ownFile != null) {
            return new FileSource<>(ownFile, format, mapper);
        }
        if (sharedFile != null) {
            return new FileSource<>(sharedFile, format, mapper, rowsOf(category));
        }
        throw new IllegalStateException("No data file is configured for " + category.getKey());
    }

    private static Predicate<DataRow> rowsOf(Category category) {
        return row -> row.find(CATEGORY_COLUMN)
                .map(category.getKey()::equalsIgnoreCase)
                .orElse(false);
    }

    private static String requireLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("File location must not be empty");
        }
        return location.strip();
    }
}
