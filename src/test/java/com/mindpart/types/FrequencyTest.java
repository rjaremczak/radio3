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
        assertTrue(Frequency.fromHz(3000).format().endsWith("Hz"));
    }

    @Test
    public void testFormatAsKHz() {
        assertTrue(Frequency.fromHz(900000).format().endsWith("kHz"));
    }

    @Test
    public void testFormatAsMHz() {
        assertTrue(Frequency.fromHz(29000600).format().endsWith("MHz"));
    }

    @Test
    public void testConstructors() {
        assertEquals(2000123, Frequency.fromMHz(2.000123).toHz());
        assertEquals(37000123, Frequency.fromKHz(37000.123).toHz());
        assertEquals(37000123, Frequency.fromHz(37000123).toHz());
    }

    @Test
    public void testConvertMethods() {
        assertEquals(32.768, Frequency.fromHz(32768).toKHz(), Double.MIN_VALUE);
        assertEquals(64.000768, Frequency.fromHz(64000768).toMHz(), Double.MIN_VALUE);
        assertEquals(3567000, Frequency.fromMHz(3.567).toHz());
    }
}