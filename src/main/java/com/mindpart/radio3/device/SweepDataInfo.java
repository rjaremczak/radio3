package com.mindpart.radio3.device;

import com.mindpart.science.Frequency;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.18
 */
public class SweepDataInfo {
    private SweepState state;
    private Frequency freqStart;
    private Frequency freqStep;
    private int numSteps;
    private SweepSignalSource source;

    public SweepDataInfo(SweepState state, Frequency freqStart, Frequency freqStep, int numSteps, SweepSignalSource source) {
        this.state = state;
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.source = source;
    }

    public SweepState getState() {
        return state;
    }

    public Frequency getFreqStart() {
        return freqStart;
    }

    public Frequency getFreqEnd() {
        return new Frequency(freqStart.value + numSteps * freqStep.value);
    }

    public Frequency getFreqStep() {
        return freqStep;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public int getNumSeries() {
        return source.getNumSeries();
    }

    public SweepSignalSource getSource() {
        return source;
    }

}
