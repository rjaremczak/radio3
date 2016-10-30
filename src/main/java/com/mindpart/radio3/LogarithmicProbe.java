package com.mindpart.radio3;

import com.mindpart.radio3.device.Adc;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.23
 */
public class LogarithmicProbe implements FrameParser<Double> {
    private static final Frame SAMPLE = new Frame(LOGPROBE_GET);

    private DeviceService deviceService;
    private Adc adc;

    public LogarithmicProbe(DeviceService deviceService) {
        this.deviceService = deviceService;
        this.adc = Adc.getDefault();
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LOGPROBE_GET;
    }

    public Double parse(int adc) {
        return -89.7 + this.adc.convert(adc) / 0.025;
    }

    @Override
    public Double parse(Frame frame) {
        return parse(Binary.toUInt16(frame.getPayload()));
    }

    public void requestData() {
        deviceService.performRequest(SAMPLE);
    }

}
