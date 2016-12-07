package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceStateSource implements FrameParser<DeviceState> {
    static final Frame GET = new Frame(FrameCommand.DEVICE_GET_STATE);

    private DeviceService deviceService;

    public DeviceStateSource(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.DEVICE_GET_STATE;
    }

    @Override
    public DeviceState parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        return new DeviceState(bi.nextBool(), bi.nextUInt16(), bi.nextUInt32(),
                AnalyserState.values()[bi.nextUInt8()],
                DdsOut.values()[bi.nextUInt8()]);
    }

    public void requestData() {
        deviceService.performRequest(DeviceStateSource.GET);
    }
}
