package com.mindpart.discrete;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MinFinderTest {
    MinFinder minTracker = new MinFinder();

    @Test
    public void testConstructor() {
        assertFalse(minTracker.isFound());
    }

    private void assertMinimum(int expectedNumber, double expectedValue) {
        assertEquals(expectedNumber, minTracker.getSample().getNumber(), Double.MIN_VALUE);
        assertEquals(expectedValue, minTracker.getSample().getValue(), Double.MIN_VALUE);
    }

    @Test
    public void testMinimum() {
        minTracker.record(0, 11.0);
        assertTrue(minTracker.isFound());
        assertMinimum(0, 11.0);

        minTracker.record(1, 5.0);
        assertMinimum(1, 5.0);

        minTracker.record(2, -3.0);
        minTracker.record(3, 1.0);
        assertMinimum(2, -3.0);
    }
}