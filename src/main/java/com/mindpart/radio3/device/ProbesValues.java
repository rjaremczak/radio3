package com.mindpart.radio3.device;

import com.mindpart.radio3.VnaResult;
import com.mindpart.type.Frequency;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbesValues {
    private double logarithmic;
    private double linear;
    private VnaResult vnaResult;
    private Frequency fMeter;

    public ProbesValues(double logarithmic, double linear, VnaResult vnaResult, Frequency fMeter) {
        this.logarithmic = logarithmic;
        this.linear = linear;
        this.vnaResult = vnaResult;
        this.fMeter = fMeter;
    }

    public double getLogarithmic() {
        return logarithmic;
    }

    public double getLinear() {
        return linear;
    }

    public VnaResult getVnaResult() {
        return vnaResult;
    }

    public Frequency getFMeter() {
        return fMeter;
    }
}
