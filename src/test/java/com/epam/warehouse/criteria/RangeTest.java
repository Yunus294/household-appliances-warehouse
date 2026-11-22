package com.epam.warehouse.criteria;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeTest {

    private final Range<Long> range = Range.of(500L, 900L);

    @Test
    void bothEndsAreIncluded() {
        assertTrue(range.contains(500L));
        assertTrue(range.contains(700L));
        assertTrue(range.contains(900L));
    }

    @Test
    void valuesOutsideAreNotContained() {
        assertFalse(range.contains(499L));
        assertFalse(range.contains(901L));
        assertFalse(range.contains(null));
    }

    @Test
    void exactRangeHoldsOneValue() {
        Range<Integer> sixteen = Range.exactly(16);

        assertTrue(sixteen.contains(16));
        assertFalse(sixteen.contains(8));
    }

    @Test
    void startCantBeGreaterThanEnd() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Range.of(900L, 500L));
        assertEquals("Range start 900 is greater than its end 500", e.getMessage());
    }

    @Test
    void endsAreRequired() {
        assertThrows(NullPointerException.class, () -> Range.of(null, 500L));
    }
}
