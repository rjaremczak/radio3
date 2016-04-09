package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class Complex {
    private double value;
    private double phase;

    public Complex(double value, double phase) {
        this.value = value;
        this.phase = phase;
    }

    public double getValue() {
        return value;
    }

    public double getPhase() {
        return phase;
    }
}
