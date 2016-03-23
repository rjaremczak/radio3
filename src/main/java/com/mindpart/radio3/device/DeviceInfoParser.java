package com.mindpart.radio3.device;

import static com.mindpart.radio3.device.FrameCommand.DEVICE_GET_INFO;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfoParser implements FrameParser<DeviceInfo> {
    static final Frame GET = new Frame(DEVICE_GET_INFO);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == DEVICE_GET_INFO;
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
