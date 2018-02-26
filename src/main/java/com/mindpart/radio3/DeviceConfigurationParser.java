package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.bin.BinaryIterator;

import static com.mindpart.radio3.device.FrameCmd.GET_DEVICE_CONFIGURATION;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceConfigurationParser implements FrameParser<DeviceConfiguration> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == GET_DEVICE_CONFIGURATION;
    }

    @Override
    public DeviceConfiguration parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        DeviceConfiguration dc = new DeviceConfiguration();
        dc.licenseOk = bi.nextBoolean();
        dc.coreUniqueId0 = bi.nextUInt32();
        dc.coreUniqueId1 = bi.nextUInt32();
        dc.coreUniqueId2 = bi.nextUInt32();
        dc.firmwareVersionMajor = bi.nextUInt8();
        dc.firmwareVersionMinor = bi.nextUInt8();
        dc.firmwareBuildTimestamp = bi.nextUInt32() * 1000;
        dc.hardwareRevision = HardwareRevision.values()[bi.nextUInt8()];
        dc.vfoType = VfoType.values()[bi.nextUInt8()];
        return dc;
    }
}