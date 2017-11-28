package com.mindpart.numeric;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.04.26
 */
public class RangeTest {
    Range range = new Range();

    @Test
    public void testConstructor() {
        assertFalse(range.isValid());
    }

    @Test
    public void testUpdate() {
        double v = -8.050183150183159;
        range.sample(v);
        assertTrue(range.isValid());
        assertEquals(v, range.min());
        assertEquals(v, range.max());
    }
}