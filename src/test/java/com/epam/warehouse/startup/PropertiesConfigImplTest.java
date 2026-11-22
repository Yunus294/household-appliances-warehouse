package com.epam.warehouse.startup;

import com.epam.warehouse.controller.ControllerFactory;
import com.epam.warehouse.controller.ControllerImpl;
import com.epam.warehouse.controller.Request;
import com.epam.warehouse.controller.Response;
import com.epam.warehouse.service.ApplianceServiceImpl;
import com.epam.warehouse.service.ServiceFactory;
import com.epam.warehouse.view.BatchViewImpl;
import com.epam.warehouse.view.ConsoleViewImpl;
import com.epam.warehouse.view.ViewFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertiesConfigImplTest {

    private static Components assemble(String... args) {
        return new PropertiesConfigImpl(LaunchOptions.parse(args)).assemble();
    }

    private static String findAll(Components components) {
        Response response = components.controller().execute(new Request("find", List.of("all"), Map.of()));
        assertTrue(response.ok(), response.text());
        return response.text().lines().findFirst().orElseThrow();
    }

    @Test
    void buildsEveryLayerFromTheSettings() {
        Components components = assemble("--config=test");

        assertInstanceOf(ApplianceServiceImpl.class, components.service());
        assertInstanceOf(ControllerImpl.class, components.controller());
        assertInstanceOf(ConsoleViewImpl.class, components.view());
        assertEquals("Found 11 appliances:", findAll(components));
    }

    @Test
    void batchOptionPicksTheBatchView() {
        assertInstanceOf(BatchViewImpl.class, assemble("--config=test", "--batch=commands.txt").view());
    }

    @ParameterizedTest
    @CsvSource({
            "single-file-test, Found 4 appliances:",
            "app,              Found 34 appliances:",
            "single-file,      Found 8 appliances:"})
    void everySettingsFileReadsItsData(String config, String expected) {
        assertEquals(expected, findAll(assemble("--config=" + config)));
    }

    @Test
    void missingSettingsFileIsReported() {
        ConfigException e = assertThrows(ConfigException.class, () -> assemble("--config=nope"));
        assertEquals("Settings file nope.properties not found", e.getMessage());
    }

    @Test
    void missingSettingIsNamed() {
        ConfigException e = assertThrows(ConfigException.class, () -> assemble("--config=missing-service"));
        assertEquals("Setting 'service' is missing in missing-service.properties", e.getMessage());
    }

    @Test
    void categoryWithoutAFileIsReported() {
        ConfigException e = assertThrows(ConfigException.class, () -> assemble("--config=no-oven-file"));
        assertEquals("No data file is configured for oven in no-oven-file.properties", e.getMessage());
    }

    @Test
    void initPublishesTheLayersThroughTheFactories() {
        new PropertiesConfigImpl(LaunchOptions.parse("--config=test")).init();

        assertInstanceOf(ApplianceServiceImpl.class, ServiceFactory.getInstance());
        assertInstanceOf(ControllerImpl.class, ControllerFactory.getInstance());
        assertInstanceOf(ConsoleViewImpl.class, ViewFactory.getInstance());
    }
}
