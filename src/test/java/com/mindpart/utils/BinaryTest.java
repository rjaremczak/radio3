package com.mindpart.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class BinaryTest {
    byte[] bytes = {(byte)0xF0, (byte)0xB2, (byte)0x12, (byte)0x0a};

    @Test
    public void testLowByteExtraction() {
        assertEquals(0xC2, Binary.uInt8low(0xA1C2));
        assertEquals(0x02, Binary.uInt8low(0x1002));
        assertEquals(0xEF, Binary.uInt8low(0xFFEF));
    }

    @Test
    public void testHighByteExtraction() {
        assertEquals(0xA1, Binary.uInt8high(0xA1C2));
        assertEquals(0x10, Binary.uInt8high(0x1002));
        assertEquals(0xFF, Binary.uInt8high(0xFFEF));
    }

    @Test
    public void testAccessWord() {
        assertEquals(0xB2F0, Binary.uInt16(bytes));
    }

    @Test
    public void testAccessByte() {
        assertEquals(0xF0, Binary.uInt8(bytes, 0));
        assertEquals(0xB2, Binary.uInt8(bytes, 1));
        assertEquals(0x12, Binary.uInt8(bytes, 2));
        assertEquals(0x0a, Binary.uInt8(bytes, 3));
    }
}