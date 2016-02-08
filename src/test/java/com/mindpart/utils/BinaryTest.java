package com.mindpart.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class BinaryTest {

    @Test
    public void testLowByteExtraction() {
        assertEquals(0xC2, Binary.lowByte(0xA1C2));
        assertEquals(0x02, Binary.lowByte(0x1002));
        assertEquals(0xEF, Binary.lowByte(0xFFEF));
    }

    @Test
    public void testHighByteExtraction() {
        assertEquals(0xA1, Binary.highByte(0xA1C2));
        assertEquals(0x10, Binary.highByte(0x1002));
        assertEquals(0xFF, Binary.highByte(0xFFEF));
    }

    @Test
    public void testWordFromBytes() {
        byte[] bytes = {(byte)0xF0, (byte)0xB2};
        assertEquals(0xB2F0, Binary.word(bytes));
    }
}