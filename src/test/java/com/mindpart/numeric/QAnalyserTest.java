package com.mindpart.numeric;

import org.junit.Test;

import static java.lang.Double.MIN_VALUE;
import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.05
 */
public class QAnalyserTest {
    final double[] freq = { 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 3.0, 3.1 };
    final double[] data = { 9.0, 8.0, 8.0, 7.0, 4.0, 1.0, 2.0, 3.0, 6.0, 5.0, 4.0, 2.0, 1.0, 3.0, 5.0, 6.0, 7.0, 8.0, 8.0, 9.0, 3.0, 1.0 };
    final QAnalyser qAnalyser = new QAnalyser(freq, data);

    @Test
    public void testLowPeak() {
        assertTrue(qAnalyser.findLowestPeak(3));
        assertEquals(1.4, qAnalyser.getStartFreq(), MIN_VALUE);
        assertEquals(1.5, qAnalyser.getPeakFreq(), MIN_VALUE);
        assertEquals(1.8, qAnalyser.getEndFreq(), MIN_VALUE);
        assertEquals(0.4, qAnalyser.getBandwidth(), 1E-6);
        assertEquals(1.5 / 0.4, qAnalyser.getQ(), 0.01);
    }

    @Test
    public void testHighPeak() {
        assertTrue(qAnalyser.findHighestPeak(3));
        assertEquals(2.5, qAnalyser.getStartFreq(), MIN_VALUE);
        assertEquals(2.9, qAnalyser.getPeakFreq(), MIN_VALUE);
        assertEquals(3.0, qAnalyser.getEndFreq(), MIN_VALUE);
        assertEquals(0.5, qAnalyser.getBandwidth(), 1E-6);
        assertEquals(2.9 / 0.5, qAnalyser.getQ(), 0.01);
    }
}