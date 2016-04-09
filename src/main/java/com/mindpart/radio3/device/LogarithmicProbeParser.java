package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_SAMPLE;
import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_SAMPLING_OFF;
import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_SAMPLING_ON;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LogarithmicProbeParser implements FrameParser<Double> {
    static final Frame SAMPLE = new Frame(LOGPROBE_SAMPLE);
    static final Frame START_SAMPLING = new Frame(LOGPROBE_SAMPLING_ON);
    static final Frame STOP_SAMPLING = new Frame(LOGPROBE_SAMPLING_OFF);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LOGPROBE_SAMPLE;
    }

    @Override
    public Double parse(Frame frame) {
        return (double)Binary.toUInt16(frame.getPayload());
    }
}
