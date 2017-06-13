package com.mindpart.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MinFinderTest {
    MinFinder<Double,Double> minTracker = new MinFinder<>();

    @Test
    public void testConstructor() {
        assertFalse(minTracker.isFound());
    }

    private void assertMinimum(double expectedX, double expectedY) {
        assertEquals(expectedX, minTracker.getFoundX(), Double.MIN_VALUE);
        assertEquals(expectedY, minTracker.getFoundY(), Double.MIN_VALUE);
    }

    @Test
    public void testMinimum() {
        minTracker.record(0.0, 11.0);
        assertTrue(minTracker.isFound());
        assertMinimum(0.0, 11.0);

        minTracker.record(0.1, 5.0);
        assertMinimum(0.1, 5.0);

        minTracker.record(0.2, -3.0);
        minTracker.record(0.3, 1.0);
        assertMinimum(0.2, -3.0);
    }
}