package com.mindpart.utils;

import org.junit.Test;

import static org.junit.Assert.*;

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
        range.update(v);
        assertTrue(range.isValid());
        assertEquals(v, range.min(), Double.MIN_VALUE);
        assertEquals(v, range.max(), Double.MIN_VALUE);
    }
}