package com.mindpart.radio3;

import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.device.*;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCmd.GET_ALL_PROBES;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbesParser implements FrameParser<Probes> {
    private final double logBaseDbm;
    private final double logRatioVToDbm;
    private final Adc logAdc;

    private final double linRefVrms;
    private final double linRatioVToVrms;
    private final Adc linAdc;

    private final Adc vnaGainAdc;
    private final Adc vnaPhaseAdc;

    private final int fmeterBase;
    private final int fmeterMultiplier;

    public ProbesParser(Configuration configuration) {
        logBaseDbm = configuration.getLogProbeConfig().getBaseDbm();
        logRatioVToDbm = configuration.getLogProbeConfig().getRatioVToDbm();
        logAdc = configuration.getLogProbeConfig().getAdcConfig().createAdc();

        linRefVrms = configuration.getLinProbeConfig().getBaseVrms();
        linRatioVToVrms = configuration.getLinProbeConfig().getRatioVToVrms();
        linAdc = configuration.getLinProbeConfig().getAdcConfig().createAdc();

        vnaGainAdc = configuration.getVnaConfig().getGainAdcConfig().createAdc();
        vnaPhaseAdc = configuration.getVnaConfig().getPhaseAdcConfig().createAdc();

        fmeterBase = configuration.getFreqMeterConfig().getBase();
        fmeterMultiplier = configuration.getFreqMeterConfig().getMultiplier();
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == GET_ALL_PROBES;
    }

    public Double convertLogValue(int adc) {
        return logBaseDbm + logAdc.convert(adc) / logRatioVToDbm;
    }

    public Double convertLinValue(int adcValue) {
        return Math.max(0, (linRefVrms + this.linAdc.convert(adcValue)) / linRatioVToVrms);
    }

    public double calculateReturnLoss(int adcValue) {
        double v = vnaGainAdc.convert(adcValue);

        if (v <= 0.3) {
            v = 0;
        } else if (v > 1.8) {
            v = 1.8;
        }

        return (v * 100/3) - 30;
    }

    public double calculatePhaseDiff(int adcValue) {
        double v = vnaPhaseAdc.convert(adcValue);

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
        return Math.toIntExact((count * fmeterMultiplier) + fmeterBase);
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
