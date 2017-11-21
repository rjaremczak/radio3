package com.mindpart.numeric;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.05
 */
public class QFactorCalcTest {
    final double[] freq = { 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 3.0, 3.1 };
    final double[] data = { 9.0, 8.0, 8.0, 7.0, 4.0, 1.0, 2.0, 3.0, 4.0, 5.0, 4.0, 2.0, 1.0, 3.0, 5.0, 6.0, 7.0, 8.0, 8.0, 9.0, 7.0, 6.0 };
    final QFactorCalc qFactorCalc = new QFactorCalc(freq, data);

    @Test
    public void testLowPeak() {
        assertTrue(qFactorCalc.findBandStop(3));
        assertEquals(1.4, qFactorCalc.getBandStart());
        assertEquals(1.5, qFactorCalc.getBandPeak());
        assertEquals(1.8, qFactorCalc.getBandEnd());
        assertEquals(0.4, qFactorCalc.getBandwidth(), 1E-6);
        assertEquals(1.5 / 0.4, qFactorCalc.getQFactor(), 0.01);
    }

    @Test
    public void testHighPeak() {
        assertTrue(qFactorCalc.findBandPass(3));
        assertEquals(2.5, qFactorCalc.getBandStart());
        assertEquals(2.9, qFactorCalc.getBandPeak());
        assertEquals(3.1, qFactorCalc.getBandEnd());
        assertEquals(0.6, qFactorCalc.getBandwidth(), 1E-6);
        assertEquals(2.9 / 0.6, qFactorCalc.getQFactor(), 0.01);
    }
}