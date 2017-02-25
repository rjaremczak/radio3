package com.mindpart.radio3;

import com.mindpart.radio3.device.Adc;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LINPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LinearParser implements FrameParser<Double> {
    private Adc adc = Adc.getDefault();

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LINPROBE_GET;
    }

    public Double parse(int adc) {
        return this.adc.convert(adc);
    }

    @Override
    public Double parse(Frame frame) {
        return parse(Binary.toUInt16(frame.getPayload()));
    }
}
