package com.epam.warehouse.controller.format;

import com.epam.warehouse.entity.Category;
import com.epam.warehouse.fixture.Appliances;
import com.epam.warehouse.service.Stock;
import com.epam.warehouse.service.StockReport;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplianceFormatterTest {

    private final ApplianceFormatter formatter = new ApplianceFormatter();

    @Test
    void listsAppliancesWithTheirDetails() {
        List<String> lines = formatter.appliances(List.of(Appliances.laptop().build(), Appliances.oven().build()))
                .lines().toList();

        assertEquals(List.of(
                "Found 2 appliances:",
                " ID  Category  Name              Price $  Qty  Total $   Kg  Details",
                "---  --------  ----------------  -------  ---  -------  ---  " + "-".repeat(83),
                "101  Laptop    ASUS Vivobook 15      649   14    9,086  1.7  "
                        + "os=windows  cpu=\"Intel Core i5-1235U\"  ram=16  storage=512  screen=15.6  battery=42",
                "201  Oven      Bosch Serie 4         549    8    4,392   34  power=3400  volume=71  temperature=275"),
                lines);
    }

    @Test
    void oneApplianceIsSingular() {
        assertEquals("Found 1 appliance:", formatter.appliances(List.of(Appliances.oven().build())).lines().findFirst()
                .orElseThrow());
    }

    @Test
    void stockHasATotalRow() {
        Map<Category, Stock> stock = new LinkedHashMap<>();
        stock.put(Category.LAPTOP, new Stock(2, 15, 11_500));
        stock.put(Category.OVEN, new Stock(2, 8, 4_400));

        assertEquals("""
                Stock:
                Category  Models  Units  Value $
                --------  ------  -----  -------
                Laptop         2     15   11,500
                Oven           2      8    4,400
                Total          4     23   15,900""", formatter.stock(new StockReport(stock)));
    }

    @Test
    void stockOfOneCategoryHasNoTotalRow() {
        assertEquals("""
                Stock:
                Category      Models  Units  Value $
                ------------  ------  -----  -------
                Refrigerator       0      0        0""",
                formatter.stock(new StockReport(Map.of(Category.REFRIGERATOR, Stock.EMPTY))));
    }
}
