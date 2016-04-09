package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class Probes {
    private double logarithmic;
    private double linear;
    private Complex complex;
    long fmeter;

    public Probes(double logarithmic, double linear, Complex complex, long fmeter) {
        this.logarithmic = logarithmic;
        this.linear = linear;
        this.complex = complex;
        this.fmeter = fmeter;
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

    public long getFmeter() {
        return fmeter;
    }
}
