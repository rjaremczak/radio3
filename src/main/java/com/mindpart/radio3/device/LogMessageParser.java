package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.21
 */
public class LogMessageParser implements FrameParser<LogMessage> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.LOG_MESSAGE;
    }

    @Override
    public LogMessage parse(Frame frame) {
        return new LogMessage(frame.binaryIterator().nextString(frame.getPayloadSize()));
    }
}
