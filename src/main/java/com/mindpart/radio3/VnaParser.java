package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.CMPPROBE_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class VnaParser implements FrameParser<VnaResult> {
    private Adc adc = Adc.getDefault();

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == CMPPROBE_GET;
    }

    public double calculateReturnLoss(int adcValue) {
        double v = adc.convert(adcValue);

        if (v <= 0.3) {
            v = 0;
        } else if (v > 1.8) {
            v = 1.8;
        }

        return (v * 100/3) - 30;
    }

    public double calculatePhaseDiff(int adcValue) {
        double v = adc.convert(adcValue);

        if (v <= 0.03) {
            v = 0;
        } else if (v > 1.8) {
            v = 1.8;
        }

        return (v * 100);
    }

    public VnaResult calculateVnaResult(int gain, int phase) {
        return new VnaResult(calculateReturnLoss(gain), calculatePhaseDiff(phase));
    }

    @Override
    public VnaResult parse(Frame frame) {
        byte[] payload = frame.getPayload();
        return calculateVnaResult(Binary.toUInt16(payload, 0), Binary.toUInt16(payload, 2));
    }
}
