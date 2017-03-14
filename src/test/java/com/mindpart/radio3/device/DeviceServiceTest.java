package com.mindpart.radio3.device;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.14
 */
public class DeviceServiceTest {
    @Test
    public void testBuildAvgMode() throws Exception {
        assertEquals(0x00, DeviceService.buildAvgMode(1,1));
        assertEquals(0x11, DeviceService.buildAvgMode(2,2));
        assertEquals(0xff, DeviceService.buildAvgMode(16,16));
    }

}