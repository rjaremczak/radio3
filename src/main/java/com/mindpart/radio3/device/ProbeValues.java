package com.mindpart.radio3.device;

import com.mindpart.types.Frequency;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbeValues {
    private double logarithmic;
    private double linear;
    private Complex complex;
    private Frequency fMeter;

    public ProbeValues(double logarithmic, double linear, Complex complex, Frequency fMeter) {
        this.logarithmic = logarithmic;
        this.linear = linear;
        this.complex = complex;
        this.fMeter = fMeter;
    }

    public double getLogarithmic() {
        return logarithmic;
    }

    public double getLinear() {
        return linear;
    }

    public Complex getComplex() {
        return complex;
    }

    public Frequency getFMeter() {
        return fMeter;
    }
}
