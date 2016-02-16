package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
class DeviceInfoParser implements FrameParser<DeviceInfo> {
    static final int TYPE = 0x000;
    static final Frame REQUEST = new Frame(TYPE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getType() == TYPE;
    }

    @Override
    public DeviceInfo parse(Frame frame) {
        return new DeviceInfo(
                frame.getWord(0),
                frame.getWord(2),
                frame.getByte(4),
                frame.getByte(5));
    }
}
