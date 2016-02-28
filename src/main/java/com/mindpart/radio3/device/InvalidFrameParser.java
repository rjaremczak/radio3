package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.24
 */
public class InvalidFrameParser implements FrameParser<String> {
    static final int TYPE = 0x03FD;

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getType() == TYPE;
    }

    @Override
    public String parse(Frame frame) {
        return "device doesn't understand last request";
    }
}
