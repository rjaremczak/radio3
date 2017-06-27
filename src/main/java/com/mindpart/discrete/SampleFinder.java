package com.mindpart.discrete;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
abstract class SampleFinder {
    protected Sample sample;

    public SampleFinder() {
        reset();
    }

    public void reset() {
        sample = null;
    }

    public boolean isFound() {
        return sample != null;
    }

    public abstract void record(int number, double value);

    public Sample getSample() {
        return sample;
    }
}
