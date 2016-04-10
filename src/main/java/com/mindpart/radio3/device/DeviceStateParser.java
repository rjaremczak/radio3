package com.mindpart.radio3.device;

import com.mindpart.utils.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceStateParser implements FrameParser<DeviceState> {
    static final Frame GET = new Frame(FrameCommand.DEVICE_GET_STATE);

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.DEVICE_GET_STATE;
    }

    @Override
    public DeviceState parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        bi.nextUInt8();
        return new DeviceState(bi.nextBool(), bi.nextUInt16(), bi.nextUInt32());
    }
}
