package com.mindpart.discrete;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.04
 */
public class ValueCheckTest {
    ValueCheck valueCheck = new ValueCheck() {
        @Override
        public void sample(int number, double value) {}
    };

    @Test
    public void testConstructor() {
        assertFalse(valueCheck.isFound());
    }
}