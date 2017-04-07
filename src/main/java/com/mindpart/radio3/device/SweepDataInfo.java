package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.18
 */
public class SweepDataInfo {
    private SweepState state;
    private long freqStart;
    private long freqStep;
    private int numSteps;
    private SweepSignalSource source;

    public SweepDataInfo(SweepState state, long freqStart, long freqStep, int numSteps, SweepSignalSource source) {
        this.state = state;
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.source = source;
    }

    public SweepState getState() {
        return state;
    }

    public long getFreqStart() {
        return freqStart;
    }

    public long getFreqEnd() {
        return freqStart + numSteps * freqStep;
    }

    public long getFreqStep() {
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
