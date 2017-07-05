package com.mindpart.radio3.ui;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LinearProbeNormContext extends LinearProbeContext {
    private double[] referenceData;

    public LinearProbeNormContext(Function<Integer, Double> valueParser, double[] referenceData, String axisLabel) {
        super(valueParser, axisLabel);
        this.referenceData = referenceData;
    }

    @Override
    public Double process(int sampleNum, Double value) {
        return value - referenceData[sampleNum];
    }
}
