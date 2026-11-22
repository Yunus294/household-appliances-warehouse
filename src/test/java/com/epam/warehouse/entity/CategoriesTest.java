package com.epam.warehouse.entity;

import com.epam.warehouse.fixture.Appliances;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriesTest {

    private final List<Appliance> oneOfEach = List.of(
            Appliances.laptop().build(),
            Appliances.oven().build(),
            Appliances.refrigerator().build(),
            Appliances.washingMachine().build());

    @Test
    void everyApplianceBelongsToTheCategoryOfItsClass() {
        for (Appliance appliance : oneOfEach) {
            assertEquals(Category.of(appliance.getClass()), appliance.getCategory());
            assertEquals(appliance.getClass(), appliance.getCategory().getType());
        }
    }

    @Test
    void categoryIsFoundByKeyIgnoringCase() {
        assertEquals(Optional.of(Category.WASHING_MACHINE), Category.byKey("Washing-Machine"));
        assertEquals(Optional.empty(), Category.byKey("toaster"));
    }

    @Test
    void abstractApplianceHasNoCategory() {
        assertThrows(IllegalArgumentException.class, () -> Category.of(Appliance.class));
    }

    @Test
    void onlyMainsAppliancesArePowerConsumable() {
        List<Boolean> powerConsumable = oneOfEach.stream().map(PowerConsumable.class::isInstance).toList();

        assertEquals(List.of(false, true, true, true), powerConsumable);
        assertEquals(3400, assertInstanceOf(PowerConsumable.class, oneOfEach.get(1)).getPowerConsumption());
    }

    @Test
    void refrigeratorsAndWashingMachinesAreEnergyRated() {
        List<Boolean> energyRated = oneOfEach.stream().map(EnergyRated.class::isInstance).toList();

        assertEquals(List.of(false, false, true, true), energyRated);
        assertEquals(EnergyClass.A, assertInstanceOf(EnergyRated.class, oneOfEach.get(3)).getEnergyClass());
    }

    @Test
    void freezerCantBeBiggerThanTheWholeRefrigerator() {
        Refrigerator.Builder builder = Appliances.refrigerator().capacityLiters(100).freezerLiters(120);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("freezer (120 l) can't be bigger than the whole capacity (100 l)", e.getMessage());
    }

    @Test
    void appliancesSurviveSerialization() throws IOException, ClassNotFoundException {
        for (Appliance appliance : oneOfEach) {
            assertEquals(appliance, copy(appliance));
        }
    }

    private static Object copy(Appliance appliance) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(appliance);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return in.readObject();
        }
    }
}
