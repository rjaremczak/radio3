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
        DeviceInfo di = new DeviceInfo();
        di.name = bi.nextString(16);
        di.buildId = bi.nextString(32);
        di.vfoName = bi.nextString(16);
        di.vfoMinFrequency = bi.nextUInt32();
        di.vfoMaxFrequency = bi.nextUInt32();
        di.fMeterName = bi.nextString(16);
        di.fMeterMinFrequency = bi.nextUInt32();
        di.fMeterMaxFrequency = bi.nextUInt32();
        return di;
    }
}
