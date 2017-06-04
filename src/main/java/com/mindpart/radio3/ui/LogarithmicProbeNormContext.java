package com.mindpart.radio3.ui;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LogarithmicProbeNormContext extends LogarithmicProbeContext {
    private double[] referenceData;

    public LogarithmicProbeNormContext(Function<Integer, Double> parser, double[] referenceData) {
        super(parser);
        this.referenceData = referenceData;
    }

    @Override
    public String label() {
        return "Relative Power [dB]";
    }

    @Override
    public Double process(int sampleNum, Double value) {
        return value - referenceData[sampleNum];
    }
}
