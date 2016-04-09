package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.PROBES_SAMPLE;
import static com.mindpart.radio3.device.FrameCommand.PROBES_SAMPLING_OFF;
import static com.mindpart.radio3.device.FrameCommand.PROBES_SAMPLING_ON;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbesParser implements FrameParser<Probes> {
    static final Frame SAMPLE = new Frame(PROBES_SAMPLE);
    static final Frame START_SAMPLING = new Frame(PROBES_SAMPLING_ON);
    static final Frame STOP_SAMPLING = new Frame(PROBES_SAMPLING_OFF);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == PROBES_SAMPLE;
    }

    @Override
    public Probes parse(Frame frame) {
        byte[] payload = frame.getPayload();
        return new Probes(
                (double) Binary.toUInt16(payload, 0),
                (double) Binary.toUInt16(payload, 2),
                new Complex(
                        (double) Binary.toUInt16(payload, 4),
                        (double) Binary.toUInt16(payload, 6)),
                Binary.toUInt32(payload, 8)
        );
    }
}
