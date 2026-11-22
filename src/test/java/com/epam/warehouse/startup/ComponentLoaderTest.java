package com.epam.warehouse.startup;

import com.epam.warehouse.controller.Controller;
import com.epam.warehouse.controller.ControllerImpl;
import com.epam.warehouse.dao.DaoRegistry;
import com.epam.warehouse.dao.format.RecordFormat;
import com.epam.warehouse.service.ApplianceServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentLoaderTest {

    private final ComponentLoader loader = new ComponentLoader();

    @Test
    void createsWithTheNoArgumentConstructor() {
        assertInstanceOf(ArrayList.class, loader.create("java.util.ArrayList", List.class));
    }

    @Test
    void passesTheDependencies() {
        Controller controller = loader.create("com.epam.warehouse.controller.ControllerImpl", Controller.class,
                new ApplianceServiceImpl(new DaoRegistry()));

        assertInstanceOf(ControllerImpl.class, controller);
    }

    @Test
    void unknownClassIsReported() {
        ConfigException e = assertThrows(ConfigException.class, () -> loader.create("com.example.Nope", List.class));
        assertEquals("Class com.example.Nope not found", e.getMessage());
    }

    @Test
    void classOfTheWrongTypeIsRejected() {
        ConfigException e = assertThrows(ConfigException.class, () -> loader.create("java.lang.String", List.class));
        assertEquals("java.lang.String is not a List", e.getMessage());
    }

    @Test
    void missingConstructorIsReported() {
        ConfigException e = assertThrows(ConfigException.class,
                () -> loader.create("com.epam.warehouse.controller.ControllerImpl", Controller.class, "text"));
        assertEquals("com.epam.warehouse.controller.ControllerImpl has no public constructor (String)", e.getMessage());
    }

    @Test
    void failingConstructorIsReported() {
        ConfigException e = assertThrows(ConfigException.class,
                () -> loader.create("com.epam.warehouse.dao.format.CsvFormat", RecordFormat.class, ""));
        assertEquals("Can't create com.epam.warehouse.dao.format.CsvFormat: Delimiter must not be empty", e.getMessage());
    }
}
