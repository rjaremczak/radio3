package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.PROBES_GET;
import static com.mindpart.radio3.device.FrameCommand.PROBES_STOP_SAMPLING;
import static com.mindpart.radio3.device.FrameCommand.PROBES_START_SAMPLING;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbesParser implements FrameParser<Probes> {
    static final Frame SAMPLE = new Frame(PROBES_GET);
    static final Frame START_SAMPLING = new Frame(PROBES_START_SAMPLING);
    static final Frame STOP_SAMPLING = new Frame(PROBES_STOP_SAMPLING);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == PROBES_GET;
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
