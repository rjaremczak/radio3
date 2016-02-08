package com.mindpart.radio3.device;

import static org.junit.Assert.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class FrameTest {

    public void testTypeOnlyConstructor() {
        Frame frame = new Frame(DeviceStatusRequest.TYPE);
        assertEquals(null, frame.getPayload());
        assertFalse(frame.getType().hasPayload());
        assertEquals(0, frame.getPayloadSize());
        assertEquals(DeviceStatusRequest.TYPE, frame.getType());
    }
}