package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
class DeviceInfoParser implements FrameParser<DeviceInfo> {
    static final int HEADER = 0x8000;
    static final Frame REQUEST = new Frame(0x0000);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getHeader() == HEADER;
    }

    @Override
    public DeviceInfo parse(Frame frame) {
        return new DeviceInfo(
                frame.getWord(0));
    }
}
