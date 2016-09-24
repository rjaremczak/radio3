package com.mindpart.radio3;

import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class FMeterUnit implements FrameParser<Long> {
    static final Frame SAMPLE = new Frame(FMETER_GET);

    private DeviceService deviceService;

    public FMeterUnit(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FMETER_GET && frame.getPayloadSize() == 4;
    }

    @Override
    public Long parse(Frame frame) {
        return Binary.toUInt32(frame.getPayload());
    }

    public void requestData() {
        deviceService.performRequest(FMeterUnit.SAMPLE);
    }
}
