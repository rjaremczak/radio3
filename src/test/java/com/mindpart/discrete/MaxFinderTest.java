package com.mindpart.discrete;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MaxFinderTest {
    MaxCheck maxFinder = new MaxCheck();

    @Test
    public void testConstructor() {
        assertFalse(maxFinder.isFound());
    }

    private void assertMaximum(int expectedNumber, double expectedValue) {
        assertEquals(expectedNumber, maxFinder.getSampleValue(), Double.MIN_VALUE);
        assertEquals(expectedValue, maxFinder.getSampleValue(), Double.MIN_VALUE);
    }

    @Test
    public void testMaximum() {
        maxFinder.sample(0, 31.0);
        assertTrue(maxFinder.isFound());
        assertMaximum(0, 31.0);

        maxFinder.sample(1, 35.0);
        assertMaximum(1, 35.0);

        maxFinder.sample(2, 43.0);
        maxFinder.sample(3, 1.0);
        assertMaximum(2, 43.0);
    }
}