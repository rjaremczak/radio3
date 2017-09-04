package com.mindpart.discrete;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
abstract class ValueCheck {
    protected double sampleValue;
    protected int sampleNumber;

    public ValueCheck() {
        reset();
    }

    public void reset() {
        sampleValue = Double.NaN;
    }

    public boolean isFound() {
        return !Double.isNaN(sampleValue);
    }

    public abstract void sample(int number, double value);

    public Double getSampleValue() {
        return sampleValue;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }
}
