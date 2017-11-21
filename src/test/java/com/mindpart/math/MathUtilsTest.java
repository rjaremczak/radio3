package com.mindpart.math;

import org.junit.jupiter.api.Test;

import static com.mindpart.math.MathUtils.tickUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.05.19
 */
public class MathUtilsTest {

    @Test
    public void testFindGridUnit() {
        assertEquals(0.5, tickUnit(7.35, 10), Double.MIN_VALUE);
        assertEquals(0.05, tickUnit(0.8, 10), Double.MIN_VALUE);
        assertEquals(0.5, tickUnit(6, 10), Double.MIN_VALUE);
    }

    @Test
    public void testHalfTickUnit() {
        assertEquals(0.5, tickUnit(2.8, 10), Double.MIN_VALUE);
    }

    @Test
    public void testQuarterTickUnit() {
        assertEquals(0.5, tickUnit(1.8, 10), Double.MIN_VALUE);
    }

}