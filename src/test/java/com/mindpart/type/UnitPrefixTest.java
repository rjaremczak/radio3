package com.mindpart.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
class UnitPrefixTest {

    @Test
    public void testAutoRangeMilli() {
        assertEquals(UnitPrefix.MILLI, UnitPrefix.autoRange(0.001));
        assertEquals(UnitPrefix.MILLI, UnitPrefix.autoRange(0.030));
        assertEquals(UnitPrefix.MILLI, UnitPrefix.autoRange(0.5994));
        assertEquals(UnitPrefix.MILLI, UnitPrefix.autoRange(0.9994));
    }

    @Test
    public void testAutoRangeHugeValue() {
        assertEquals(UnitPrefix.TERA, UnitPrefix.autoRange(34E20));
        assertEquals(UnitPrefix.TERA, UnitPrefix.autoRange(1E12));
        assertEquals(UnitPrefix.TERA, UnitPrefix.autoRange(123.2E13));
    }

    @Test
    public void testFrom() {

    }

}