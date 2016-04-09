package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_SAMPLE;
import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_SAMPLING_OFF;
import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_SAMPLING_ON;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class ComplexProbeParser implements FrameParser<Complex> {
    static final Frame SAMPLE = new Frame(CMPPROBE_SAMPLE);
    static final Frame START_SAMPLING = new Frame(CMPPROBE_SAMPLING_ON);
    static final Frame STOP_SAMPLING = new Frame(CMPPROBE_SAMPLING_OFF);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == CMPPROBE_SAMPLE;
    }

    @Override
    public Complex parse(Frame frame) {
        byte[] payload = frame.getPayload();
        double gain = Binary.toUInt16(payload, 0);
        double phase = Binary.toUInt16(payload, 2);
        return new Complex(gain, phase);
    }
}
