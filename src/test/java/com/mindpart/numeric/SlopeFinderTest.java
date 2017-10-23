package com.mindpart.numeric;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.02
 */
public class SlopeFinderTest {
    final double[] frequency    = { 7.0, 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8, 7.9, 8.0, 8.1, 8.2, 8.3, 8.4, 8.5, 8.6 };
    final double[] dataRising   = { 3.0, 2.7, 2.3, 2.0, 1.6, 1.2, 1.1, 1.2, 1.3, 1.4, 1.3, 1.4, 1.5, 1.6, 1.7, 2.0, 2.5 };
    final double[] dataFalling  = { 1.0, 1.1, 1.2, 1.4, 1.6, 1.8, 2.5, 3.1, 3.0, 2.3, 2.1, 2.0, 2.0, 2.1, 1.6, 1.4, 1.3 };

    @Test
    public void testRisingForward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataRising);
        assertTrue(slopeFinder.findRisingForward(8, 1.65));
        assertEquals(14, slopeFinder.getSampleNumber());
        assertEquals(8.35, slopeFinder.linearInterpolation(frequency, 1.65));
    }

    @Test
    public void testRisingBackward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataRising);
        assertTrue(slopeFinder.findRisingBackward(8, 2.5));
        assertEquals(1, slopeFinder.getSampleNumber());
        assertEquals(7.15, slopeFinder.linearInterpolation(frequency, 2.5));
    }

    @Test
    public void testFallingForward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataFalling);
        assertTrue(slopeFinder.findFallingForward(8, 1.85));
        assertEquals(14, slopeFinder.getSampleNumber());
        assertEquals(8.35, slopeFinder.linearInterpolation(frequency, 1.85), 0.00001);
    }

    @Test
    public void testFallingBackward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataFalling);
        assertTrue(slopeFinder.findFallingBackward(8, 1.3));
        assertEquals(2, slopeFinder.getSampleNumber());
        assertEquals(7.25, slopeFinder.linearInterpolation(frequency, 1.3));
    }
}