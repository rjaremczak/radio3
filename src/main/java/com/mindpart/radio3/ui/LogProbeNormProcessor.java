package com.mindpart.radio3.ui;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LogProbeNormProcessor extends LogProbeProcessor {
    private double[] referenceData;

    public LogProbeNormProcessor(Function<Integer, Double> parser, double[] referenceData, String axisLabel) {
        super(parser, axisLabel);
        this.referenceData = referenceData;
    }

    @Override
    public Double process(int sampleNum, Double value) {
        return value - referenceData[sampleNum];
    }
}
