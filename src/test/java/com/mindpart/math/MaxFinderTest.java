package com.mindpart.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MaxFinderTest {
    MaxFinder<Double,Double> maxFinder = new MaxFinder<>();

    @Test
    public void testConstructor() {
        assertFalse(maxFinder.isFound());
    }

    private void assertMaximum(double expectedX, double expectedY) {
        assertEquals(expectedX, maxFinder.getFoundX(), Double.MIN_VALUE);
        assertEquals(expectedY, maxFinder.getFoundY(), Double.MIN_VALUE);
    }

    @Test
    public void testMaximum() {
        maxFinder.record(0.0, 31.0);
        assertTrue(maxFinder.isFound());
        assertMaximum(0.0, 31.0);

        maxFinder.record(0.1, 35.0);
        assertMaximum(0.1, 35.0);

        maxFinder.record(0.2, 43.0);
        maxFinder.record(0.3, 1.0);
        assertMaximum(0.2, 43.0);
    }
}