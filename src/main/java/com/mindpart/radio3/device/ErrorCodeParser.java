package com.mindpart.radio3.device;

import java.util.Arrays;
import java.util.Collection;

import static com.mindpart.radio3.device.FrameCommand.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.21
 */
public class ErrorCodeParser implements FrameParser<ErrorCode> {
    private static Collection<FrameCommand> errorCodes = Arrays.asList(ERROR_INVALID_FRAME);

    @Override
    public boolean recognizes(Frame frame) {
        return errorCodes.contains(frame.getCommand());
    }

    @Override
    public ErrorCode parse(Frame frame) {
        return frame.getPayloadSize() == 0 ? new ErrorCode(frame.getCommand()) : new ErrorCode(frame.getCommand(), frame.binaryIterator().nextUInt8());
    }
}
