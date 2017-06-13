package com.mindpart.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class AvgCalcTest {
    AvgCalc avgCalc = new AvgCalc();

    @Test
    public void testConstructor() {
        assertFalse(avgCalc.ready());
    }

    @Test
    public void testCalculate() {
        avgCalc.add(1.0);
        assertTrue(avgCalc.ready());
        assertEquals(1.0, avgCalc.getAvg(), Double.MIN_VALUE);
        avgCalc.add(2.0);
        avgCalc.add(3.0);
        assertEquals(2.0, avgCalc.getAvg(), Double.MIN_VALUE);
    }
}