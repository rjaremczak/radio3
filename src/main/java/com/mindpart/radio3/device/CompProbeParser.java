package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class CompProbeParser implements FrameParser<GainPhase> {
    static final int READ_TYPE = 0x005;
    static final Frame READ_REQUEST = new Frame(READ_TYPE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getType() == READ_TYPE;
    }

    @Override
    public GainPhase parse(Frame frame) {
        byte[] payload = frame.getPayload();
        double gain = Binary.toUInt16(payload, 0);
        double phase = Binary.toUInt16(payload, 2);
        return new GainPhase(gain, phase);
    }
}
