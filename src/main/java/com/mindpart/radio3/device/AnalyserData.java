package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.16
 */
public class AnalyserData {
    public enum Source {
        LOG_PROBE("Logarithmic",1, "Power [dBm]"),
        LIN_PROBE("Linear",1, "Power [mW]"),
        COMPLEX_PROBE("Complex", 2, "Gain [dB]", "Phase [°]"),
        COMPLEX_LOG_PROBE("Complex + Log.", 3, "Gain [dB]", "Phase [°]", "Power [dBm]");

        private int numSeries;
        private String title;
        private String seriesTitle[];

        Source(String title, int numSeries, String... seriesTitles) {
            this.title = title;
            this.numSeries = numSeries;
            this.seriesTitle = seriesTitles;
            if(seriesTitles==null || seriesTitles.length < numSeries) {
                throw new IllegalArgumentException("series titles must be specified");
            }
        }

        public int getNumSeries() {
            return numSeries;
        }

        public String getSeriesTitle(int series) {
            return seriesTitle[series];
        }

        public String toString() {
            return title;
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
