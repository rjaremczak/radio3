package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class ProbeValues {
    private double logarithmic;
    private double linear;
    private Complex complex;
    private double fMeter;

    public ProbeValues(double logarithmic, double linear, Complex complex, double fMeter) {
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

    public double getFMeter() {
        return fMeter;
    }
}
