package com.mindpart.radio3.device;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.24
 */
public class AdcTest {

    private double adcToVoltage(int adcValue) {
        double multiplier = (3.45 - 0.0) / (4035 - 0);
        return 0.0 + adcValue * multiplier;
    }

    @Test
    public void testDefaultConverter() {
        Adc defaultConverter = Adc.getDefault();

        assertEquals(adcToVoltage(0), defaultConverter.convert(0), Double.MIN_VALUE);
        assertEquals(adcToVoltage(2000), defaultConverter.convert(2000), Double.MIN_VALUE);
        assertEquals(adcToVoltage(4035), defaultConverter.convert(4035), Double.MIN_VALUE);
    }

}