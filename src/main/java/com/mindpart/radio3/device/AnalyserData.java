package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class AnalyserData {
    public enum Mode {
        LOG_PROBE("Logarithmic",1), LIN_PROBE("Linear",1), COMPLEX_PROBE("Complex", 2), COMPLEX_LOG_PROBE("Complex + Log.", 3);

        private int numSeries;
        private String name;

        Mode(String name, int numSeries) {
            this.name = name;
            this.numSeries = numSeries;
        }

        public int getNumSeries() {
            return numSeries;
        }

        public String toString() {
            return name;
        }
    }

    private long freqStart;
    private long freqStep;
    private int numSteps;
    private Mode mode;
    private int data[][];

    public AnalyserData(long freqStart, long freqStep, int numSteps, Mode mode) {
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.mode = mode;
        this.data = new int[mode.getNumSeries()][numSteps+1];
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
        return mode.getNumSeries();
    }

    public Mode getMode() {
        return mode;
    }

    public int[][] getData() {
        return data;
    }
}
