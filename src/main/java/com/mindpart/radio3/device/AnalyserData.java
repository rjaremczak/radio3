package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class AnalyserData {
    public enum Source {
        LOG_PROBE("Logarithmic",1), LIN_PROBE("Linear",1), COMPLEX_PROBE("Complex", 2), COMPLEX_LOG_PROBE("Complex + Log.", 3);

        private int numSeries;
        private String name;

        Source(String name, int numSeries) {
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
    private Source source;
    private int data[][];

    public AnalyserData(long freqStart, long freqStep, int numSteps, Source source) {
        this.freqStart = freqStart;
        this.freqStep = freqStep;
        this.numSteps = numSteps;
        this.source = source;
        this.data = new int[source.getNumSeries()][numSteps+1];
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
        return source.getNumSeries();
    }

    public Source getSource() {
        return source;
    }

    public int[][] getData() {
        return data;
    }
}
