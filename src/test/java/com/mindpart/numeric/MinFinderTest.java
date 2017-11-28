package com.mindpart.numeric;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MinFinderTest {
    MinCheck minFinder = new MinCheck();

    @Test
    public void testConstructor() {
        assertFalse(minFinder.isFound());
    }

    private void assertMinimum(int expectedNumber, double expectedValue) {
        assertEquals(expectedNumber, minFinder.getSampleNumber());
        assertEquals(expectedValue, minFinder.getSampleValue());
    }

    @Test
    public void testMinimum() {
        minFinder.sample(0, 11.0);
        assertTrue(minFinder.isFound());
        assertMinimum(0, 11.0);

        minFinder.sample(1, 5.0);
        assertMinimum(1, 5.0);

        minFinder.sample(2, -3.0);
        minFinder.sample(3, 1.0);
        assertMinimum(2, -3.0);
    }
}