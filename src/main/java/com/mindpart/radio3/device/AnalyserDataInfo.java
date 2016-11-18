package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.18
 */
public class AnalyserDataInfo {
    private long freqStart;
    private long freqStep;
    private int numSteps;
    private AnalyserDataSource source;

    public AnalyserDataInfo(long freqStart, long freqStep, int numSteps, AnalyserDataSource source) {
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.source = source;
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

    public AnalyserDataSource getSource() {
        return source;
    }

}
