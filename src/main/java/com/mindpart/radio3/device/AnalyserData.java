package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class AnalyserData {
    private long freqStart;
    private long freqStep;
    private int numSteps;
    private int numGraphs;
    private int graphData[][];

    public AnalyserData(long freqStart, long freqStep, int numSteps, int numGraphs) {
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.numGraphs = numGraphs;
        this.graphData = new int[numGraphs][];
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

    public int getNumGraphs() {
        return numGraphs;
    }

    public int[] getGraphData(int graph) {
        return graphData[graph];
    }
}
