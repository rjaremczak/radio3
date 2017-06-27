package com.mindpart.discrete;

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
        assertFalse(avgCalc.isReady());
    }

    @Test
    public void testCalculate() {
        avgCalc.record(1.0);
        assertTrue(avgCalc.isReady());
        assertEquals(1.0, avgCalc.getAvg(), Double.MIN_VALUE);
        avgCalc.record(2.0);
        avgCalc.record(3.0);
        assertEquals(2.0, avgCalc.getAvg(), Double.MIN_VALUE);
    }
}