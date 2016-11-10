package com.mindpart.types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
public class FrequencyTest {
    @Test
    public void testFormatAsHz() {
        assertTrue(Frequency.ofHz(300).format().endsWith(" Hz"));
    }

    @Test
    public void testFormatAsKHz() {
        assertTrue(Frequency.ofHz(900000).format().endsWith(" kHz"));
    }

    @Test
    public void testFormatAsMHz() {
        assertTrue(Frequency.ofHz(29000600).format().endsWith(" MHz"));
    }

    @Test
    public void testConstructors() {
        assertEquals(2000123, Frequency.ofMHz(2.000123).toHz());
        assertEquals(37000123, Frequency.ofKHz(37000.123).toHz());
        assertEquals(37000123, Frequency.ofHz(37000123).toHz());
    }

    @Test
    public void testConvertMethods() {
        assertEquals(32.768, Frequency.ofHz(32768).toKHz(), Double.MIN_VALUE);
        assertEquals(64.000768, Frequency.ofHz(64000768).toMHz(), Double.MIN_VALUE);
        assertEquals(3567000, Frequency.ofMHz(3.567).toHz());
    }

    @Test
    public void testParse() {
        assertEquals(1800000, Frequency.parse(" 1.80 MHz ").toHz());
        assertEquals(2801, Frequency.parse(" 2.801 kHz ").toHz());
        assertEquals(803, Frequency.parse(" 803 Hz ").toHz());
    }
}