package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class AnalyserData {
    private long freqStart;
    private long freqStep;
    private int numSteps;
    private int numSeries;
    private int data[][];

    public AnalyserData(long freqStart, long freqStep, int numSteps, int numSeries) {
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.numSeries = numSeries;
        this.data = new int[numSeries][numSteps];
    }

    public long getFreqStart() {
        return freqStart;
    }

    public long getFreqStep() {
        return freqStep;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public int getNumSeries() {
        return numSeries;
    }

    public int[][] getData() {
        return data;
    }
}
