package com.epam.warehouse.dao;

import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The only test that initializes the static {@link DaoFactory}; the other tests build their DAOs
 * directly, so they don't depend on the order tests run in.
 */
class DaoFactoryTest {

    @Test
    void secondInitDoesNotReplaceTheFirst() throws DaoException {
        ApplianceDao<Laptop> laptops = criteria -> List.of();
        DaoFactory.init(new DaoRegistry().register(Laptop.class, laptops));

        DaoFactory.init(new DaoRegistry().register(Oven.class, criteria -> List.of()));

        assertSame(laptops, DaoFactory.getInstance(Laptop.class));
        assertThrows(DaoException.class, () -> DaoFactory.getInstance(Oven.class));
    }
}
