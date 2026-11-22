package com.epam.warehouse.dao;

import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.dao.format.CsvFormat;
import com.epam.warehouse.dao.format.KeyValueFormat;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.entity.Refrigerator;
import com.epam.warehouse.entity.WashingMachine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStorageTest {

    private static <A extends Appliance> int count(DaoRegistry registry, Class<A> type) throws DaoException {
        return registry.get(type).find(SearchCriteria.create()).size();
    }

    @Test
    void fileForEachCategory() throws DaoException {
        DaoRegistry registry = new FileStorage(new CsvFormat())
                .file(Category.LAPTOP, "laptops-test.csv")
                .file(Category.OVEN, "ovens-test.csv")
                .file(Category.REFRIGERATOR, "refrigerators-test.csv")
                .file(Category.WASHING_MACHINE, "washing-machines-test.csv")
                .build();

        assertEquals(4, count(registry, Laptop.class));
        assertEquals(3, count(registry, Oven.class));
        assertEquals(2, count(registry, Refrigerator.class));
        assertEquals(2, count(registry, WashingMachine.class));
    }

    @Test
    void oneSharedFile() throws DaoException {
        DaoRegistry registry = new FileStorage(new KeyValueFormat()).sharedFile("warehouse-test.txt").build();

        assertEquals(1, count(registry, Laptop.class));
        assertEquals(1, count(registry, Oven.class));
        assertEquals(1, count(registry, Refrigerator.class));
        assertEquals(1, count(registry, WashingMachine.class));
    }

    @Test
    void ownFileWinsOverTheSharedOne() throws DaoException {
        DaoRegistry registry = new FileStorage(new KeyValueFormat())
                .sharedFile("warehouse-test.txt")
                .file(Category.LAPTOP, "no-such-file.txt")
                .build();

        assertEquals(1, count(registry, Oven.class));
        assertThrows(DaoException.class, () -> count(registry, Laptop.class));
    }

    @Test
    void everyCategoryNeedsAFile() {
        FileStorage storage = new FileStorage(new CsvFormat()).file(Category.LAPTOP, "laptops-test.csv");

        IllegalStateException e = assertThrows(IllegalStateException.class, storage::build);
        assertEquals("No data file is configured for oven", e.getMessage());
    }

    @Test
    void registryListsTypesInCategoryOrder() {
        DaoRegistry registry = new FileStorage(new KeyValueFormat()).sharedFile("warehouse-test.txt").build();

        assertEquals(List.of(Laptop.class, Oven.class, Refrigerator.class, WashingMachine.class),
                List.copyOf(registry.types()));
    }
}
