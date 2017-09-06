package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.04
 */
public class QAnalyser {
    private final double[] data;
    private final double[] freq;
    private final SlopeFinder slopeFinder;
    private final LocalExtremaFinder localExtremaFinder;

    private int startSample;
    private int peakSample;
    private int endSample;

    public QAnalyser(double[] data, double[] freq) {
        this.data = data;
        this.freq = freq;
        this.slopeFinder = new SlopeFinder(this.data);
        this.localExtremaFinder = new LocalExtremaFinder(data);
    }

    public boolean analyseLowPeak(double minDepth) {
        Extremum extremum = localExtremaFinder.getLowestMinimum();
        if(extremum!=null) {
            int peak = extremum.getNumber();
            double threshold = data[peak] + minDepth;
            if(slopeFinder.findRisingBackward(peak, threshold)) {
                startSample = slopeFinder.getSampleNumber();
                if(slopeFinder.findRisingForward(peak, threshold)) {
                    endSample = slopeFinder.getSampleNumber();
                    peakSample = peak;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean analyseHighPeak(double minHeight) {
        Extremum extremum = localExtremaFinder.getHighestMaximum();
        if(extremum!=null) {
            int peak = extremum.getNumber();
            double threshold = data[peak] - minHeight;
            if(slopeFinder.findFallingBackward(peak, threshold)) {
                startSample = slopeFinder.getSampleNumber();
                if(slopeFinder.findFallingForward(peak, threshold)) {
                    endSample = slopeFinder.getSampleNumber();
                    peakSample = peak;
                    return true;
                }
            }
        }
        return false;
    }

    public double getStartFreq() {
        return freq[startSample];
    }

    public double getPeakFreq() {
        return freq[peakSample];
    }

    public double getEndFreq() {
        return freq[endSample];
    }

    public double getBandwidth() {
        return getEndFreq() - getStartFreq();
    }

    public double getQ() {
        return getPeakFreq() / getBandwidth();
    }
}
