package com.mindpart.numeric;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.02
 */
public class SlopeFinderTest {
    final double[] dataRising =  { 3.0, 2.5, 2.2, 2.0, 1.6, 1.2, 1.1, 1.2, 1.3, 1.4, 1.3, 1.4, 1.5, 1.6, 1.7, 2.0, 2.5 };
    final double[] dataFalling = { 1.1, 1.2, 1.3, 1.4, 1.6, 1.8, 2.5, 3.1, 3.0, 2.3, 2.1, 2.0, 2.0, 2.1, 1.8, 1.4, 1.3 };

    @Test
    public void testRisingForward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataRising);
        assertTrue(slopeFinder.findRisingForward(8, 1.7));
        assertEquals(14, slopeFinder.getSampleNumber());
    }

    @Test
    public void testRisingBackward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataRising);
        assertTrue(slopeFinder.findRisingBackward(8, 2.5));
        assertEquals(1, slopeFinder.getSampleNumber());
    }

    @Test
    public void testFallingForward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataFalling);
        assertTrue(slopeFinder.findFallingForward(8, 1.8));
        assertEquals(14, slopeFinder.getSampleNumber());
    }

    @Test
    public void testFallingBackward() {
        SlopeFinder slopeFinder = new SlopeFinder(dataFalling);
        assertTrue(slopeFinder.findFallingBackward(8, 1.3));
        assertEquals(2, slopeFinder.getSampleNumber());
    }
}