package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.bin.BinaryIterator;

import static com.mindpart.radio3.device.FrameCommand.DEVICE_GET_INFO;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfoParser implements FrameParser<DeviceInfo> {
    private DeviceInfo deviceInfo = null;

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
        di.hardwareRevision = HardwareRevision.values()[bi.nextUInt8()];
        di.vfoType = VfoType.values()[bi.nextUInt8()];
        di.baudRate = bi.nextUInt32();
        deviceInfo = di;
        return di;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void resetDeviceInfo() {
        deviceInfo = null;
    }

    public boolean isDeviceInfo() {
        return deviceInfo != null;
    }
}