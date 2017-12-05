package com.mindpart.radio3.device;

import com.mindpart.science.Frequency;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class SweepResponse extends SweepDataInfo {
    public SweepResponse(SweepState state, Frequency freqStart, Frequency freqStep, int numSteps, SweepSignalSource source) {
        super(state, freqStart, freqStep, numSteps, source);
        this.data = new int[source.getNumSeries()][numSteps+1];
    }

    private int data[][];

    public int[][] getData() {
        return data;
    }

    public SweepDataInfo toInfo() {
        return new SweepDataInfo(getState(), getFreqStart(), getFreqStep(), getNumSteps(), getSource());
    }
}
