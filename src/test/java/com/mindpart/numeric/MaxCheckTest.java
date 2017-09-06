package com.mindpart.numeric;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MaxCheckTest {
    MaxCheck maxCheck = new MaxCheck();

    @Test
    public void testConstructor() {
        assertFalse(maxCheck.isFound());
    }

    private void assertMaximum(int expectedNumber, double expectedValue) {
        assertEquals(expectedNumber, maxCheck.getSampleNumber(), Double.MIN_VALUE);
        assertEquals(expectedValue, maxCheck.getSampleValue(), Double.MIN_VALUE);
    }

    @Test
    public void testMaximum() {
        maxCheck.sample(0, 31.0);
        assertTrue(maxCheck.isFound());
        assertMaximum(0, 31.0);

        maxCheck.sample(1, 35.0);
        assertMaximum(1, 35.0);

        maxCheck.sample(2, 43.0);
        maxCheck.sample(3, 1.0);
        assertMaximum(2, 43.0);
    }
}