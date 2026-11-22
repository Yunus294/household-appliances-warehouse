package com.epam.warehouse.controller.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextTableTest {

    @Test
    void alignsColumns() {
        String table = new TextTable("Name", "Qty").alignRight(1)
                .addRow("Oven", "8")
                .addRow("Fridge", "12")
                .render();

        assertEquals("""
                Name    Qty
                ------  ---
                Oven      8
                Fridge   12""", table);
    }

    @Test
    void linesHaveNoTrailingSpaces() {
        assertEquals("""
                Key  Value
                ---  -----
                a""", new TextTable("Key", "Value").addRow("a", "").render());
    }

    @Test
    void rowMustFitTheColumns() {
        assertThrows(IllegalArgumentException.class, () -> new TextTable("A", "B").addRow("only one"));
    }
}
