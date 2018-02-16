package com.mindpart.radio3;

import com.mindpart.radio3.config.FreqMeterConfig;
import com.mindpart.radio3.device.*;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCmd.GET_ALL_PROBES;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbesParser implements FrameParser<Probes> {
    private final FreqMeterConfig freqMeterConfig;

    private double logRefDbm = -87;
    private double logVToDbmRatio = 0.025;
    private Adc logAdc = Adc.getDefault();

    private double linRefVrms = -0.0265;
    private double linVToVrmsRatio = 7.5 / 1.80;
    private Adc linAdc = Adc.getDefault();

    private Adc vnaAdc = Adc.getDefault();

    public ProbesParser(FreqMeterConfig freqMeterConfig) {
        this.freqMeterConfig = freqMeterConfig;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == GET_ALL_PROBES;
    }

    public Double convertLogValue(int adc) {
        return logRefDbm + logAdc.convert(adc) / logVToDbmRatio;
    }

    public Double convertLinValue(int adcValue) {
        return Math.max(0, (linRefVrms + this.linAdc.convert(adcValue)) / linVToVrmsRatio);
    }

    public double calculateReturnLoss(int adcValue) {
        double v = vnaAdc.convert(adcValue);

        if (v <= 0.3) {
            v = 0;
        } else if (v > 1.8) {
            v = 1.8;
        }

        return (v * 100/3) - 30;
    }

    public double calculatePhaseDiff(int adcValue) {
        double v = vnaAdc.convert(adcValue);

        if (v <= 0.03) {
            v = 0;
        } else if (v > 1.8) {
            v = 1.8;
        }

        return (v * 100);
    }

    public VnaResult convertVnaValue(int gain, int phase) {
        return new VnaResult(calculateReturnLoss(gain), calculatePhaseDiff(phase));
    }

    Integer convertFreqMeterValue(long count) {
        return Math.toIntExact((count * freqMeterConfig.multiplier) + freqMeterConfig.base);
    }

    Probes parse(int logarithmic, int linear, int vnaGain, int vnaPhase, long freqMeter) {
        return new Probes(
                convertLogValue(logarithmic),
                convertLinValue(linear),
                convertVnaValue(vnaGain, vnaPhase),
                convertFreqMeterValue(freqMeter)
        );
    }

    @Override
    public Probes parse(Frame frame) {
        byte[] payload = frame.getPayload();
        return parse(Binary.toUInt16(payload, 0),
                Binary.toUInt16(payload, 2),
                Binary.toUInt16(payload, 4),
                Binary.toUInt16(payload, 6),
                Binary.toUInt32(payload, 8)
        );
    }
}
