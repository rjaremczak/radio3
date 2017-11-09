package com.mindpart.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
class MetricPrefixTest {

    @Test
    public void testAutoRangeMilli() {
        assertEquals(MetricPrefix.MILLI, MetricPrefix.autoRange(0.001));
        assertEquals(MetricPrefix.MILLI, MetricPrefix.autoRange(0.030));
        assertEquals(MetricPrefix.MILLI, MetricPrefix.autoRange(0.5994));
        assertEquals(MetricPrefix.MILLI, MetricPrefix.autoRange(0.9994));
    }

    @Test
    public void testAutoRangeHugeValue() {
        assertEquals(MetricPrefix.TERA, MetricPrefix.autoRange(34E20));
        assertEquals(MetricPrefix.TERA, MetricPrefix.autoRange(1E12));
        assertEquals(MetricPrefix.TERA, MetricPrefix.autoRange(123.2E13));
    }

}