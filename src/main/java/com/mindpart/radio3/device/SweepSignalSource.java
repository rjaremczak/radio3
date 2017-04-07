package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.18
 */
public enum SweepSignalSource {
    LOG_PROBE("Logarithmic",1, "Power [dBm]"),
    LIN_PROBE("Linear",1, "Power [mW]"),
    VNA("VNA", 2, "SWR", "Phase [°]");

    private int numSeries;
    private String title;
    private String seriesTitle[];

    SweepSignalSource(String title, int numSeries, String... seriesTitles) {
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
