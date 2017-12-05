package com.mindpart.science;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.11
 */
public class PowerTest {
    double accuracy = 0.0000001;

    @Test
    public void testConstructors() {
        assertEquals(1.013, Power.ofWatt(1.013).toWatt(), accuracy);
        assertEquals(904.316, Power.ofMilliWatt(904316).toWatt(), accuracy);
        assertEquals(0.001, Power.ofDBm(0).toWatt(), accuracy);
        assertEquals(13.2, Power.ofDBm(13.2).toDBm(), accuracy);
    }

    @Test
    public void testFormat() {
        assertTrue(Power.ofWatt(123).format().endsWith("W"));
        assertTrue(Power.ofWatt(0.999).format().endsWith("mW"));
        assertTrue(Power.ofWatt(1).formatDBm().endsWith("dBm"));
    }

    @Test
    public void testConvertMethods() {
        assertEquals(32768100, Power.ofWatt(32768.1).toMilliWatt(), accuracy);
        assertEquals(655.352, Power.ofMilliWatt(655352).toWatt(), accuracy);
        assertEquals(0, Power.ofMilliWatt(1.0).toDBm(), accuracy);
        assertEquals(1000, Power.ofDBm(30).toMilliWatt(), accuracy);
    }

    @Test
    public void testParse() {
        assertEquals(1000, Power.parse("1000 W").toWatt(), accuracy);
        assertEquals(123, Power.parse("123 mW").toMilliWatt(), accuracy);
        assertEquals(1, Power.parse("0 dBm").toMilliWatt(), accuracy);
        assertEquals(1, Power.parse("30 dBm").toWatt(), accuracy);
    }
}