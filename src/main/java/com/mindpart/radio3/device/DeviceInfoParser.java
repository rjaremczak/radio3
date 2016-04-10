package com.mindpart.radio3.device;

import com.mindpart.utils.BinaryIterator;

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
        BinaryIterator bi = frame.binaryIterator();
        return new DeviceInfo(
                bi.nextString(16), bi.nextUInt16(),
                bi.nextString(16), bi.nextUInt16()*1000000, bi.nextUInt16()*1000000,
                bi.nextString(16), bi.nextUInt16()*1000000, bi.nextUInt16()*1000000);
    }
}
