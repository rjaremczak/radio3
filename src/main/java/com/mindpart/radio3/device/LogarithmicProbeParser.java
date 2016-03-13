package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LogarithmicProbeParser implements FrameParser<Double> {
    static final int READ_TYPE = 0x003;
    static final Frame READ_REQUEST = new Frame(READ_TYPE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getType() == READ_TYPE;
    }

    @Override
    public Double parse(Frame frame) {
        return null;
    }
}
