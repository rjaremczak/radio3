package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class FMeterParser implements FrameParser<Long> {
    static final Frame SAMPLE = new Frame(FMETER_GET);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FMETER_GET && frame.getPayloadSize() == 4;
    }

    @Override
    public Long parse(Frame frame) {
        return Binary.toUInt32(frame.getPayload());
    }
}
