package com.mindpart.radio3;

import com.mindpart.radio3.device.AdcConverter;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LINPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LinearProbe implements FrameParser<Double> {
    static final Frame SAMPLE = new Frame(LINPROBE_GET);

    private DeviceService deviceService;
    private AdcConverter adcConverter;

    public LinearProbe(DeviceService deviceService) {
        this.deviceService = deviceService;
        this.adcConverter = AdcConverter.getDefault();
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LINPROBE_GET;
    }

    public Double fromAdc(int adc) {
        return adcConverter.convert(adc);
        //return adcConverter.convert(adc) * 0.133;
    }

    @Override
    public Double parse(Frame frame) {
        return fromAdc(Binary.toUInt16(frame.getPayload()));
    }

    public void requestData() {
        deviceService.performRequest(LinearProbe.SAMPLE);
    }
}
