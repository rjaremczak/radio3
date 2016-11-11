package com.mindpart.types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.11
 */
public class VoltageTest {
    double accuracy = 0.0000001;

    @Test
    public void testConstructors() {
        assertEquals(2.015, Voltage.ofVolt(2.015).toVolt(), accuracy);
        assertEquals(0.715, Voltage.ofVolt(0.715).toVolt(), accuracy);
        assertEquals(1.715, Voltage.ofMilliVolt(1715).toVolt(), accuracy);
    }

    @Test
    public void testFormat() {
        assertTrue(Voltage.ofVolt(1.234).format().endsWith("V"));
        assertTrue(Voltage.ofVolt(0.037).format().endsWith("mV"));
    }

    @Test
    public void testConvertMethods() {
        assertEquals(23456, Voltage.ofVolt(23.456).toMilliVolt(), accuracy);
        assertEquals(123.456, Voltage.ofMilliVolt(123456).toVolt(), accuracy);
    }

    @Test
    public void testParse() {
        assertEquals(800.345, Voltage.parse("800.345 V").toVolt(), accuracy);
        assertEquals(800.34512, Voltage.parse("800345.12 mV").toVolt(), accuracy);
    }
}