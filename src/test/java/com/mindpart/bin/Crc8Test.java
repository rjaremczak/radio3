package com.mindpart.bin;

import com.mindpart.bin.Crc8;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.04.07
 */

public class Crc8Test {

    @Test
    public void testCalculation() {
        Crc8 crc8 = new Crc8();
        byte[] data = { 0x1a, 0x1b, 0x2f, (byte) 0xff, 0x01, 0x23};
        crc8.process(data);
        assertEquals(0xa5, crc8.getCrc());
    }

}