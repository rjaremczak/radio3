package com.mindpart.radio3;

import com.mindpart.radio3.device.Adc;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCommand.LINPROBE_DATA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class LinearParser implements FrameParser<Double> {
    private static double REF_VRMS = -0.0265;
    private static double V_TO_VRMS_RATIO = 7.5 / 1.80;

    private Adc adc = Adc.getDefault();

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LINPROBE_DATA;
    }

    @Override
    public Double parse(Frame frame) {
        return parse(Binary.toUInt16(frame.getPayload()));
    }

    public Double parse(int adcValue) {
        return Math.max(0, (REF_VRMS + this.adc.convert(adcValue)) / V_TO_VRMS_RATIO);
    }
}
