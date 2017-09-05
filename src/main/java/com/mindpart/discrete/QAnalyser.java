package com.mindpart.discrete;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.04
 */
public class QAnalyser {
    private final double[] data;
    private final double[] freq;
    private final SlopeFinder slopeFinder;

    private int startSample;
    private int peakSample;
    private int endSample;

    public QAnalyser(double[] data, double[] freq) {
        this.data = data;
        this.freq = freq;
        this.slopeFinder = new SlopeFinder(this.data);
    }

    public boolean analyseLowPeak(int peak, double minDepth) {
        double threshold = data[peak] + minDepth;
        if(slopeFinder.findRisingBackward(peak, threshold)) {
            startSample = slopeFinder.getSampleNumber();
            if(slopeFinder.findRisingForward(peak, threshold)) {
                endSample = slopeFinder.getSampleNumber();
                peakSample = peak;
                return true;
            }
        }
        return false;
    }

    public boolean analyseHighPeak(int peak, double minHeight) {
        double threshold = data[peak] - minHeight;
        if(slopeFinder.findFallingBackward(peak, threshold)) {
            startSample = slopeFinder.getSampleNumber();
            if(slopeFinder.findFallingForward(peak, threshold)) {
                endSample = slopeFinder.getSampleNumber();
                peakSample = peak;
                return true;
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
