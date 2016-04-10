package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LogarithmicProbeParser implements FrameParser<Double> {
    static final Frame SAMPLE = new Frame(LOGPROBE_GET);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LOGPROBE_GET;
    }

    @Override
    public Double parse(Frame frame) {
        return (double)Binary.toUInt16(frame.getPayload());
    }
}
