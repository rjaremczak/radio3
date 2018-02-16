package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.bin.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceStateParser implements FrameParser<DeviceState> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCmd.GET_DEVICE_STATE;
    }


    @Override
    public DeviceState parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        DeviceState ds = new DeviceState();
        ds.timeMs = bi.nextUInt32();
        ds.vfoToVna = bi.nextBoolean();
        ds.amplifier = bi.nextBoolean();

        int attVal = bi.nextUInt8();
        ds.att6dB = (attVal & 1) != 0;
        ds.att10dB = (attVal & 2) != 0;
        ds.att20dB = (attVal & 4) != 0;

        return ds;
    }
}
