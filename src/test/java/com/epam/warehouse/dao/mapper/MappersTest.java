package com.epam.warehouse.dao.mapper;

import com.epam.warehouse.dao.format.DataRow;
import com.epam.warehouse.dao.format.RecordFormatException;
import com.epam.warehouse.entity.Laptop;
import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MappersTest {

    private static Map<String, String> common(String id, String brand, String model, String price,
                                              String quantity, String weight) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("brand", brand);
        values.put("model", model);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("weight", weight);
        return values;
    }

    private static Map<String, String> laptopValues() {
        Map<String, String> values = common("101", "ASUS", "Vivobook 15", "649", "14", "1.7");
        values.put("os", "windows");
        values.put("cpu", "Intel Core i5-1235U");
        values.put("ram", "16");
        values.put("storage", "512");
        values.put("screen", "15.6");
        values.put("battery", "42");
        return values;
    }

    private static Laptop mapLaptop(Map<String, String> values) {
        return new LaptopMapper().map(new DataRow(2, values));
    }

    @Test
    void mapsLaptop() {
        assertEquals(Appliances.laptop().build(), mapLaptop(laptopValues()));
    }

    @Test
    void mapsOven() {
        Map<String, String> values = common("201", "Bosch", "Serie 4", "549", "8", "34");
        values.put("power", "3400");
        values.put("volume", "71");
        values.put("temperature", "275");

        assertEquals(Appliances.oven().build(), new OvenMapper().map(new DataRow(2, values)));
    }

    @Test
    void mapsRefrigerator() {
        Map<String, String> values = common("301", "Samsung", "RB38T675ESA", "899", "7", "70");
        values.put("power", "120");
        values.put("energy", "c");
        values.put("capacity", "385");
        values.put("freezer", "114");
        values.put("nofrost", "yes");

        assertEquals(Appliances.refrigerator().build(), new RefrigeratorMapper().map(new DataRow(2, values)));
    }

    @Test
    void mapsWashingMachine() {
        Map<String, String> values = common("401", "Bosch", "Serie 6", "599", "8", "72");
        values.put("power", "2300");
        values.put("energy", "A");
        values.put("load", "9");
        values.put("spin", "1400");

        assertEquals(Appliances.washingMachine().build(), new WashingMachineMapper().map(new DataRow(2, values)));
    }

    @Test
    void mapperKnowsItsType() {
        assertEquals(Laptop.class, new LaptopMapper().type());
    }

    @Test
    void wrongOsIsRejected() {
        Map<String, String> values = laptopValues();
        values.put("os", "winxp");

        assertThrows(RecordFormatException.class, () -> mapLaptop(values));
    }

    @Test
    void zeroWeightIsRejected() {
        Map<String, String> values = laptopValues();
        values.put("weight", "0.0");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> mapLaptop(values));
        assertEquals("weight must be a positive number, but was 0.0", e.getMessage());
    }

    @Test
    void priceInWrongFormatIsRejected() {
        Map<String, String> values = laptopValues();
        values.put("price", "450.00");

        assertThrows(RecordFormatException.class, () -> mapLaptop(values));
    }

    @Test
    void missingColumnIsRejected() {
        Map<String, String> values = laptopValues();
        values.remove("battery");

        RecordFormatException e = assertThrows(RecordFormatException.class, () -> mapLaptop(values));
        assertEquals("column 'battery' is missing", e.getMessage());
    }
}
