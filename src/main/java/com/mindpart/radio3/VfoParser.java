package com.mindpart.radio3;

import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.type.Frequency;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCommand.VFO_GET_FREQ;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class VfoParser implements FrameParser<Integer> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == VFO_GET_FREQ && frame.getPayloadSize() == 4;
    }

    @Override
    public Integer parse(Frame frame) {
        return Math.toIntExact(Binary.toUInt32(frame.getPayload()));
    }
}
