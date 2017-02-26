package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class VnaParser implements FrameParser<Complex> {
    private Adc adc = Adc.getDefault();

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == CMPPROBE_GET;
    }

    public double calculateSWR(int adcValue) {
        double v = adc.convert(adcValue);
        double dB = -32.0 + (v - 0.03)/0.03;
        double ratio = 1/Math.pow(10,dB/20);
        return Math.abs((1+ratio)/(1-ratio));
    }

    public double calculatePhaseAngle(int adcValue) {
        double v = adc.convert(adcValue);
        double phaseDiff = (v - 0.03)/0.01;
        return phaseDiff;
    }

    Complex parse(int gain, int phase) {
        return new Complex(calculateSWR(gain), calculatePhaseAngle(phase));
    }

    @Override
    public Complex parse(Frame frame) {
        byte[] payload = frame.getPayload();
        return parse(Binary.toUInt16(payload, 0), Binary.toUInt16(payload, 2));
    }
}