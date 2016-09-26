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
public class FMeterProbe implements FrameParser<Double> {
    static final Frame SAMPLE = new Frame(FMETER_GET);

    private DeviceService deviceService;

    public FMeterProbe(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FMETER_GET && frame.getPayloadSize() == 4;
    }

    Double fromAdc(long adc) {
        return ((double)adc) / 1000000;
    }

    @Override
    public Double parse(Frame frame) {
        return fromAdc(Binary.toUInt32(frame.getPayload()));
    }

    public void requestData() {
        deviceService.performRequest(FMeterProbe.SAMPLE);
    }
}
