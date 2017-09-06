package com.mindpart.numeric;

import org.junit.Test;

import static com.mindpart.numeric.Extremum.Type.MAXIMUM;
import static com.mindpart.numeric.Extremum.Type.MINIMUM;
import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.06
 */
public class ExtremumTest {
    @Test
    public void testEqual() {
        assertEquals(new Extremum(3, MINIMUM), new Extremum(3, MINIMUM));
        assertEquals(new Extremum(3, MAXIMUM), new Extremum(3, MAXIMUM));
        assertEquals(new Extremum(13, MINIMUM), new Extremum(13, MINIMUM));
    }

    @Test
    public void testNotEqual() {
        assertNotSame(new Extremum(3, MINIMUM), new Extremum(3, MAXIMUM));
        assertNotSame(new Extremum(33, MAXIMUM), new Extremum(103, MAXIMUM));
        assertNotSame(new Extremum(1, MINIMUM), new Extremum(2, MAXIMUM));
    }
}