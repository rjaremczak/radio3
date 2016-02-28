package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class FMeterParser implements FrameParser<Long> {
    static final int READ_TYPE = 0x002;
    static final Frame READ_REQUEST = new Frame(READ_TYPE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getType() == READ_TYPE && frame.getPayloadSize() == 4;
    }

    @Override
    public Long parse(Frame frame) {
        return Binary.toUInt32(frame.getPayload());
    }
}
