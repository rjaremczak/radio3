package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_SAMPLE;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class ComplexProbeParser implements FrameParser<GainPhase> {
    static final Frame SAMPLE = new Frame(CMPPROBE_SAMPLE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == CMPPROBE_SAMPLE;
    }

    @Override
    public GainPhase parse(Frame frame) {
        byte[] payload = frame.getPayload();
        double gain = Binary.toUInt16(payload, 0);
        double phase = Binary.toUInt16(payload, 2);
        return new GainPhase(gain, phase);
    }
}
