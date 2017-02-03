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
        ds.probesSampling = bi.nextBool();
        ds.samplingPeriodMs = bi.nextInt16();
        ds.timeMs = bi.nextUInt32();
        ds.analyserState = AnalyserState.values()[bi.nextUInt8()];
        ds.vfoOut = VfoOut.values()[bi.nextUInt8()];
        ds.vfoAttenuator = VfoAttenuator.values()[bi.nextUInt8()];
        return ds;
    }

}
