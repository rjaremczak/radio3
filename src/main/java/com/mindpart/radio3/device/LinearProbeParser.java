package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LINPROBE_SAMPLE;
import static com.mindpart.radio3.device.FrameCommand.LINPROBE_SAMPLING_OFF;
import static com.mindpart.radio3.device.FrameCommand.LINPROBE_SAMPLING_ON;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LinearProbeParser implements FrameParser<Double> {
    static final Frame SAMPLE = new Frame(LINPROBE_SAMPLE);
    static final Frame START_SAMPLING = new Frame(LINPROBE_SAMPLING_ON);
    static final Frame STOP_SAMPLING = new Frame(LINPROBE_SAMPLING_OFF);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LINPROBE_SAMPLE;
    }

    @Override
    public Double parse(Frame frame) {
        return (double) Binary.toUInt16(frame.getPayload());
    }
}
