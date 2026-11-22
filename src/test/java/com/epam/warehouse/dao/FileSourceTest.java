package com.epam.warehouse.dao;

import com.epam.warehouse.criteria.Parameter;
import com.epam.warehouse.criteria.SearchCriteria;
import com.epam.warehouse.criteria.TextParameter;
import com.epam.warehouse.dao.format.CsvFormat;
import com.epam.warehouse.dao.format.KeyValueFormat;
import com.epam.warehouse.dao.mapper.LaptopMapper;
import com.epam.warehouse.dao.mapper.OvenMapper;
import com.epam.warehouse.entity.Appliance;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.entity.Oven;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSourceTest {

    private final ApplianceDao<Laptop> laptops = laptopsFrom("laptops-test.csv");

    private static ApplianceDao<Laptop> laptopsFrom(String location) {
        return new ApplianceDaoImpl<>(new FileSource<>(location, new CsvFormat(), new LaptopMapper()));
    }

    private static List<Long> ids(List<? extends Appliance> appliances) {
        return appliances.stream().map(Appliance::getId).toList();
    }

    @Test
    void emptyCriteriaFindEverythingInFileOrder() throws DaoException {
        assertEquals(List.of(1L, 2L, 3L, 4L), ids(laptops.find(SearchCriteria.create())));
    }

    @Test
    void anyAndNoneParameters() throws DaoException {
        assertEquals(4, laptops.find(SearchCriteria.<Laptop>create().add(Parameter.any())).size());
        assertTrue(laptops.find(SearchCriteria.<Laptop>create().add(Parameter.none())).isEmpty());
    }

    @Test
    void findsByCpu() throws DaoException {
        SearchCriteria<Laptop> intel = SearchCriteria.<Laptop>create()
                .add(new TextParameter<Laptop>("cpu", Laptop::getCpu, "intel"));

        assertEquals(List.of(1L, 4L), ids(laptops.find(intel)));
    }

    @Test
    void findsWithALambdaParameter() throws DaoException {
        SearchCriteria<Laptop> apple = SearchCriteria.<Laptop>create()
                .add(laptop -> "Apple M2".equals(laptop.getCpu()));

        assertEquals(List.of(3L), ids(laptops.find(apple)));
    }

    @Test
    void missingFileIsReported() {
        DaoException e = assertThrows(DaoException.class,
                () -> laptopsFrom("no-such-file.csv").find(SearchCriteria.create()));
        assertEquals("Data file not found: no-such-file.csv", e.getMessage());
    }

    @Test
    void brokenRecordNamesTheFileAndLine() {
        DaoException e = assertThrows(DaoException.class,
                () -> laptopsFrom("broken-laptops.csv").find(SearchCriteria.create()));
        assertEquals("broken-laptops.csv, line 3: 'price' must be a whole number, but was '450.00'", e.getMessage());
    }

    @Test
    void readsAFileOnDisk(@TempDir Path folder) throws IOException, DaoException {
        Path file = folder.resolve("ovens.csv");
        Files.writeString(file, "id;brand;model;price;quantity;weight;power;volume;temperature\n"
                + "201;Bosch;Serie 4;549;8;34;3400;71;275\n");
        ApplianceDao<Oven> ovens = new ApplianceDaoImpl<>(new FileSource<>(file.toString(), new CsvFormat(), new OvenMapper()));

        assertEquals(List.of(201L), ids(ovens.find(SearchCriteria.create())));
    }

    @Test
    void rowFilterPicksRowsOfASharedFile() throws DaoException {
        ApplianceDao<Oven> ovens = new ApplianceDaoImpl<>(new FileSource<>("warehouse-test.txt",
                new KeyValueFormat(), new OvenMapper(), row -> row.text("category").equals("oven")));

        assertEquals(List.of(11L), ids(ovens.find(SearchCriteria.create())));
    }
}
