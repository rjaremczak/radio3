package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class GainPhaseParser implements FrameParser<GainPhase> {
    static final int READ_TYPE = 0x005;
    static final Frame READ_REQUEST = new Frame(READ_TYPE);

    @Override
    public boolean recognizes(Frame frame) {
        return false;
    }

    @Override
    public GainPhase parse(Frame frame) {
        return null;
    }
}
