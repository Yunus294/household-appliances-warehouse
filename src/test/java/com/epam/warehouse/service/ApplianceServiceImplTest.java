package com.epam.warehouse.service;

import com.epam.warehouse.criteria.Range;
import com.epam.warehouse.criteria.RangeParameter;
import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.dao.ApplianceDao;
import com.epam.warehouse.dao.DaoException;
import com.epam.warehouse.dao.DaoRegistry;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Category;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import com.epam.warehouse.entity.Refrigerator;
import com.epam.warehouse.entity.WashingMachine;
import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplianceServiceImplTest {

    private static final Laptop LAPTOP_650 = Appliances.laptop().id(1).price(650).quantity(10).build();
    private static final Laptop LAPTOP_1000 = Appliances.laptop().id(2).price(1000).quantity(5).build();
    private static final Oven OVEN_550 = Appliances.oven().id(11).price(550).quantity(8).build();
    private static final Oven OVEN_650 = Appliances.oven().id(12).price(650).quantity(0).build();

    private static final Comparator<Appliance> BY_PRICE = Comparator.comparingLong(Appliance::getPrice);
    private static final Set<Category> ALL = EnumSet.allOf(Category.class);

    private final ApplianceService service = new ApplianceServiceImpl(new DaoRegistry()
            .register(Laptop.class, stored(LAPTOP_650, LAPTOP_1000))
            .register(Oven.class, stored(OVEN_550, OVEN_650))
            .register(Refrigerator.class, stored())
            .register(WashingMachine.class, stored()));

    /**
     * A DAO over a fixed list, standing in for the files.
     */
    @SafeVarargs
    private static <A extends Appliance> ApplianceDao<A> stored(A... appliances) {
        List<A> items = List.of(appliances);
        return criteria -> items.stream().filter(criteria::test).toList();
    }

    private static SearchCriteria<Appliance> priceFrom(long from) {
        return SearchCriteria.<Appliance>create()
                .add(new RangeParameter<>("price", Appliance::getPrice, Range.of(from, Long.MAX_VALUE)));
    }

    @Test
    void findsInOneCategory() throws ServiceException {
        assertEquals(List.of(LAPTOP_650, LAPTOP_1000),
                service.find(Set.of(Category.LAPTOP), SearchCriteria.create(), BY_PRICE));
    }

    @Test
    void mergesCategoriesAndSortsTiesById() throws ServiceException {
        assertEquals(List.of(OVEN_550, LAPTOP_650, OVEN_650, LAPTOP_1000),
                service.find(ALL, SearchCriteria.create(), BY_PRICE));
    }

    @Test
    void reversedOrderStillSortsTiesById() throws ServiceException {
        assertEquals(List.of(LAPTOP_1000, LAPTOP_650, OVEN_650, OVEN_550),
                service.find(ALL, SearchCriteria.create(), BY_PRICE.reversed()));
    }

    @Test
    void appliesTheCriteria() throws ServiceException {
        assertEquals(List.of(LAPTOP_650, OVEN_650, LAPTOP_1000), service.find(ALL, priceFrom(600), BY_PRICE));
    }

    @Test
    void repeatedCategoryIsSearchedOnce() throws ServiceException {
        assertEquals(2, service.find(List.of(Category.LAPTOP, Category.LAPTOP), SearchCriteria.create(), BY_PRICE).size());
    }

    @Test
    void resultCantBeChanged() throws ServiceException {
        List<Appliance> found = service.find(ALL, SearchCriteria.create(), BY_PRICE);

        assertThrows(UnsupportedOperationException.class, () -> found.add(LAPTOP_650));
    }

    @Test
    void stockReportCountsModelsUnitsAndValue() throws ServiceException {
        StockReport report = service.stockReport(List.of(Category.LAPTOP, Category.OVEN), SearchCriteria.create());

        assertEquals(Map.of(Category.LAPTOP, new Stock(2, 15, 11_500), Category.OVEN, new Stock(2, 8, 4_400)),
                report.byCategory());
        assertEquals(new Stock(4, 23, 15_900), report.total());
    }

    @Test
    void stockReportAppliesTheCriteria() throws ServiceException {
        StockReport report = service.stockReport(ALL, priceFrom(600));

        assertEquals(new Stock(3, 15, 11_500), report.total());
    }

    @Test
    void stockReportKeepsEmptyCategories() throws ServiceException {
        StockReport report = service.stockReport(Set.of(Category.REFRIGERATOR), SearchCriteria.create());

        assertEquals(Map.of(Category.REFRIGERATOR, Stock.EMPTY), report.byCategory());
    }

    @Test
    void daoFailureBecomesServiceException() {
        DaoException failure = new DaoException("laptops.csv is gone");
        ApplianceService broken = new ApplianceServiceImpl(new DaoRegistry().register(Laptop.class, criteria -> {
            throw failure;
        }));

        ServiceException e = assertThrows(ServiceException.class,
                () -> broken.find(Set.of(Category.LAPTOP), SearchCriteria.create(), BY_PRICE));
        assertEquals("Warehouse data is unavailable: laptops.csv is gone", e.getMessage());
        assertSame(failure, e.getCause());
    }

    @Test
    void categoryWithoutDaoIsAServiceFailure() {
        ApplianceService laptopsOnly = new ApplianceServiceImpl(new DaoRegistry().register(Laptop.class, stored()));

        assertThrows(ServiceException.class, () -> laptopsOnly.stockReport(ALL, SearchCriteria.create()));
    }

    @Test
    void nullArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.find(null, SearchCriteria.create(), BY_PRICE));
        assertThrows(IllegalArgumentException.class, () -> service.find(ALL, null, BY_PRICE));
        assertThrows(IllegalArgumentException.class, () -> service.find(ALL, SearchCriteria.create(), null));
        assertThrows(IllegalArgumentException.class, () -> service.stockReport(ALL, null));
        assertThrows(IllegalArgumentException.class, () -> new ApplianceServiceImpl(null));
    }
}
