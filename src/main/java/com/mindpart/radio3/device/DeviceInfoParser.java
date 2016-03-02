package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
class DeviceInfoParser implements FrameParser<DeviceInfo> {
    static final int READ_TYPE = 0x000;
    static final Frame READ_REQUEST = new Frame(READ_TYPE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getType() == READ_TYPE;
    }

    @Override
    public DeviceInfo parse(Frame frame) {
        return new DeviceInfo(
                frame.getUInt32(0),
                frame.getUInt16(4),
                frame.getUInt16(6),
                frame.getUInt8(8),
                frame.getUInt8(9));
    }
}
