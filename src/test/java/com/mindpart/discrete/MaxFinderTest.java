package com.mindpart.discrete;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MaxFinderTest {
    MaxFinder maxFinder = new MaxFinder();

    @Test
    public void testConstructor() {
        assertFalse(maxFinder.isFound());
    }

    private void assertMaximum(int expectedNumber, double expectedValue) {
        assertEquals(expectedNumber, maxFinder.getSample().getNumber(), Double.MIN_VALUE);
        assertEquals(expectedValue, maxFinder.getSample().getValue(), Double.MIN_VALUE);
    }

    @Test
    public void testMaximum() {
        maxFinder.record(0, 31.0);
        assertTrue(maxFinder.isFound());
        assertMaximum(0, 31.0);

        maxFinder.record(1, 35.0);
        assertMaximum(1, 35.0);

        maxFinder.record(2, 43.0);
        maxFinder.record(3, 1.0);
        assertMaximum(2, 43.0);
    }
}