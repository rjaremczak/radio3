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
        assertEquals(0xC2, Binary.toUInt8low(0xA1C2));
        assertEquals(0x02, Binary.toUInt8low(0x1002));
        assertEquals(0xEF, Binary.toUInt8low(0xFFEF));
    }

    @Test
    public void testHighByteExtraction() {
        assertEquals(0xA1, Binary.toUInt8high(0xA1C2));
        assertEquals(0x10, Binary.toUInt8high(0x1002));
        assertEquals(0xFF, Binary.toUInt8high(0xFFEF));
    }

    @Test
    public void testAccessWord() {
        assertEquals(0xB2F0, Binary.toUInt16(bytes));
    }

    @Test
    public void testAccessByte() {
        assertEquals(0xF0, Binary.toUInt8(bytes, 0));
        assertEquals(0xB2, Binary.toUInt8(bytes, 1));
        assertEquals(0x12, Binary.toUInt8(bytes, 2));
        assertEquals(0x0a, Binary.toUInt8(bytes, 3));
    }

    @Test
    public void testFromUInt32() {
        int val = 0xa1b2c3d4;
        byte[] bytes = Binary.fromUInt32(val);
        assertEquals((byte)0xd4, bytes[0]);
        assertEquals((byte)0xc3, bytes[1]);
        assertEquals((byte)0xb2, bytes[2]);
        assertEquals((byte)0xa1, bytes[3]);
    }

    @Test
    public void testToUInt32() {
        byte[] bytes = {(byte)0x01, (byte)0x1b, (byte)0x2c, (byte)0x3d};
        assertEquals(0x3d2c1b0a, bytes);
    }
}