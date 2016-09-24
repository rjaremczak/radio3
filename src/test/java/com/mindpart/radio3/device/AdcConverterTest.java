package com.mindpart.radio3.device;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.24
 */
public class AdcConverterTest {

    private double adcToVoltage(int adcValue) {
        return ((3.277)*((double)adcValue))/4035.0;
    }

    @Test
    public void testDefaultConverter() {
        AdcConverter defaultConverter = AdcConverter.getDefault();

        assertEquals(adcToVoltage(0), defaultConverter.convert(0), Double.MIN_VALUE);
        assertEquals(adcToVoltage(2000), defaultConverter.convert(2000), Double.MIN_VALUE);
        assertEquals(adcToVoltage(4035), defaultConverter.convert(4035), Double.MIN_VALUE);
    }

}