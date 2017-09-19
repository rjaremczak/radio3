package com.mindpart.numeric;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.06
 */
public class LocalExtremaFinderTest {
    final double[] data = { 1, 2, 5, 7, 9, 8, 7, 5, 3, 2, 4, 5, 8, 8.5, 6, 3, 1, 0, 3, 6, 9, 8 };
    final LocalExtremaFinder localExtremaFinder = new LocalExtremaFinder(data);

    @Test
    public void testFindMinima() {
        assertFalse(localExtremaFinder.getMinima().isEmpty());
        assertTrue(localExtremaFinder.getMinima().contains(9));
        assertTrue(localExtremaFinder.getMinima().contains(17));
        assertFalse(localExtremaFinder.getMinima().contains(0));
    }

    @Test
    public void testFindMaxima() {
        assertFalse(localExtremaFinder.getMaxima().isEmpty());
        assertTrue(localExtremaFinder.getMaxima().contains(4));
        assertTrue(localExtremaFinder.getMaxima().contains(13));
        assertFalse(localExtremaFinder.getMaxima().contains(2));
        assertFalse(localExtremaFinder.getMaxima().contains(21));
    }

    @Test
    public void testFindLowestMinimum() {
        assertEquals(17, (int)localExtremaFinder.getMinimaFromLowest().get(0));
    }

    @Test
    public void testFindHighestMaximum() {
        assertEquals(4, (int)localExtremaFinder.getMaximaFromHighest().get(0));
    }
}