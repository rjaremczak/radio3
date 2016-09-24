package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class ComplexProbe implements FrameParser<Complex> {
    static final Frame SAMPLE = new Frame(CMPPROBE_GET);

    private DeviceService deviceService;
    private AdcConverter adcConverter;

    public ComplexProbe(DeviceService deviceService) {
        this.deviceService = deviceService;
        this.adcConverter = AdcConverter.getDefault();
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == CMPPROBE_GET;
    }

    @Override
    public Complex parse(Frame frame) {
        byte[] payload = frame.getPayload();
        double gain = adcConverter.convert(Binary.toUInt16(payload, 0));
        double phase = adcConverter.convert(Binary.toUInt16(payload, 2));
        return new Complex(gain, phase);
    }

    public void requestData() {
        deviceService.performRequest(ComplexProbe.SAMPLE);
    }
}
