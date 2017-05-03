package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceStateParser implements FrameParser<DeviceState> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.DEVICE_GET_STATE;
    }

    @Override
    public DeviceState parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        DeviceState ds = new DeviceState();
        ds.timeMs = bi.nextUInt32();
        ds.vfoOut = VfoOut.values()[bi.nextUInt8()];
        ds.vfoAmp = VfoAmp.values()[bi.nextUInt8()];

        int attVal = bi.nextUInt8();
        ds.vfoAtt0 = (attVal & 1) != 0;
        ds.vfoAtt1 = (attVal & 2) != 0;
        ds.vfoAtt2 = (attVal & 4) != 0;
        return ds;
    }

}
