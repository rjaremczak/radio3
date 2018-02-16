package com.mindpart.radio3;

import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;

import static com.mindpart.radio3.device.FrameCmd.PING;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.24
 */
public class PingParser implements FrameParser<Class<Void>> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame != null && frame.getCommand() == PING && frame.getPayloadSize() == 0;
    }

    @Override
    public Class<Void> parse(Frame frame) {
        return Void.TYPE;
    }
}
