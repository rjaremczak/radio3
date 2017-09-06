package com.mindpart.numeric;

import org.junit.Before;
import org.junit.Test;

import static com.mindpart.numeric.Extremum.Type.MAXIMUM;
import static com.mindpart.numeric.Extremum.Type.MINIMUM;
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
        assertFalse(localExtremaFinder.getExtrema().isEmpty());
        assertTrue(localExtremaFinder.getExtrema().contains(new Extremum(9, MINIMUM)));
        assertTrue(localExtremaFinder.getExtrema().contains(new Extremum(17, MINIMUM)));
        assertFalse(localExtremaFinder.getExtrema().contains(new Extremum(0, MINIMUM)));
    }

    @Test
    public void testFindMaxima() {
        assertFalse(localExtremaFinder.getExtrema().isEmpty());
        assertTrue(localExtremaFinder.getExtrema().contains(new Extremum(4, MAXIMUM)));
        assertTrue(localExtremaFinder.getExtrema().contains(new Extremum(13, MAXIMUM)));
        assertFalse(localExtremaFinder.getExtrema().contains(new Extremum(2, MAXIMUM)));
        assertFalse(localExtremaFinder.getExtrema().contains(new Extremum(21, MAXIMUM)));
    }

    @Test
    public void testFindLowestMinimum() {
        assertEquals(new Extremum(17, MINIMUM), localExtremaFinder.getLowestMinimum());
    }

    @Test
    public void testFindHighestMaximum() {
        assertEquals(new Extremum(4, MAXIMUM), localExtremaFinder.getHighestMaximum());
    }
}