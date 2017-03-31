package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class AnalyserResponse extends AnalyserDataInfo {
    public AnalyserResponse(AnalyserState state, long freqStart, long freqStep, int numSteps, AnalyserDataSource source) {
        super(state, freqStart, freqStep, numSteps, source);
        this.data = new int[source.getNumSeries()][numSteps+1];
    }

    private int data[][];

    public int[][] getData() {
        return data;
    }

    public AnalyserDataInfo toInfo() {
        return new AnalyserDataInfo(getState(), getFreqStart(), getFreqStep(), getNumSteps(), getSource());
    }
}
