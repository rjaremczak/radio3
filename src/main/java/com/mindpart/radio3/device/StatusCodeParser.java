package com.mindpart.radio3.device;

import java.util.Arrays;
import java.util.Collection;

import static com.mindpart.radio3.device.FrameCommand.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.21
 */
public class StatusCodeParser implements FrameParser<StatusCode> {
    private static Collection<FrameCommand> statusCodes = Arrays.asList(STATUS_OK, STATUS_INVALID_FRAME);

    @Override
    public boolean recognizes(Frame frame) {
        return statusCodes.contains(frame.getCommand());
    }

    @Override
    public StatusCode parse(Frame frame) {
        return frame.getPayloadSize() == 0 ? new StatusCode(frame.getCommand()) : new StatusCode(frame.getCommand(), frame.binaryIterator().nextUInt8());
    }
}
