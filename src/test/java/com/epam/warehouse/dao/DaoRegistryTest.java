package com.epam.warehouse.dao;

import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DaoRegistryTest {

    @Test
    void returnsTheRegisteredDao() throws DaoException {
        ApplianceDao<Laptop> laptops = criteria -> List.of();
        DaoRegistry registry = new DaoRegistry().register(Laptop.class, laptops);

        assertSame(laptops, registry.get(Laptop.class));
    }

    @Test
    void unknownTypeIsReported() {
        DaoRegistry registry = new DaoRegistry().register(Laptop.class, criteria -> List.of());

        DaoException e = assertThrows(DaoException.class, () -> registry.get(Oven.class));
        assertEquals("No data source is configured for Oven", e.getMessage());
    }
}
