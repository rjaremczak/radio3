package com.mindpart.radio3.device;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.16
 */
public class FrameHeaderTest {

    @Test
    public void testFormatA() {
        FrameHeader header = new FrameHeader(0x6000);
        assertTrue(header.isFormatA());
        assertFalse(header.isFormatB());
        assertFalse(header.isFormatC());
        assertEquals(0, header.getCommand());
        assertEquals(6, header.getPayloadSize());
    }

    @Test
    public void testFormatB() {
        FrameHeader header = new FrameHeader(0xE011);
        header.setSizeBytes(new byte[]{(byte)200});
        assertFalse(header.isFormatA());
        assertTrue(header.isFormatB());
        assertFalse(header.isFormatC());
        assertEquals(0x11, header.getCommand());
        assertEquals(214, header.getPayloadSize());


        header = new FrameHeader(0xE013);
        header.setSizeBytes(new byte[]{(byte)0});
        assertEquals(0x13, header.getCommand());
        assertEquals(14, header.getPayloadSize());
    }

    @Test
    public void testFormatC() {
        FrameHeader header = new FrameHeader(0xF011);
        header.setSizeBytes(new byte[]{(byte)200, (byte)4});
        assertFalse(header.isFormatA());
        assertFalse(header.isFormatB());
        assertTrue(header.isFormatC());
        assertEquals(0x11, header.getCommand());
        assertEquals(1494, header.getPayloadSize());

        header = new FrameHeader(0xF013);
        header.setSizeBytes(new byte[]{(byte)0, (byte)0});
        assertEquals(0x13, header.getCommand());
        assertEquals(270, header.getPayloadSize());
    }
}