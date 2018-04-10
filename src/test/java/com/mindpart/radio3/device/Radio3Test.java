package com.mindpart.radio3.device;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.14
 */
public class Radio3Test {
    @Test
    public void testBuildAvgMode() {
        assertEquals(0x00, Radio3.buildAvgMode(1,1));
        assertEquals(0x11, Radio3.buildAvgMode(2,2));
        assertEquals(0xff, Radio3.buildAvgMode(16,16));
    }

}