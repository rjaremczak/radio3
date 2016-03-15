package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.13
 */
public class GainPhase {
    private double gain;
    private double phase;

    public GainPhase(double gain, double phase) {
        this.gain = gain;
        this.phase = phase;
    }

    public double getGain() {
        return gain;
    }

    public double getPhase() {
        return phase;
    }
}
