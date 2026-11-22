package com.epam.warehouse.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceHolderTest {

    private final InstanceHolder<String> holder = new InstanceHolder<>("Greeting");

    @Test
    void returnsTheInitializedInstance() {
        assertTrue(holder.init("hello"));
        assertEquals("hello", holder.get());
    }

    @Test
    void secondInitIsIgnored() {
        holder.init("hello");
        assertFalse(holder.init("bye"));
        assertEquals("hello", holder.get());
    }

    @Test
    void getBeforeInitFails() {
        IllegalStateException e = assertThrows(IllegalStateException.class, holder::get);
        assertEquals("Greeting is not initialized", e.getMessage());
    }

    @Test
    void nullIsRejected() {
        assertThrows(NullPointerException.class, () -> holder.init(null));
    }
}
