package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.VFO_GET_FREQ;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class VfoReadFrequencyParser implements FrameParser<Long> {
    static final Frame SAMPLE = new Frame(VFO_GET_FREQ);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == VFO_GET_FREQ && frame.getPayloadSize() == 4;
    }

    @Override
    public Long parse(Frame frame) {
        return Binary.toUInt32(frame.getPayload());
    }
}
