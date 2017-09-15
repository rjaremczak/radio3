package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.04
 */
public class QFactorCalc {
    private final double[] freq;
    private final double[] data;
    private final SlopeFinder slopeFinder;
    private final LocalExtremaFinder localExtremaFinder;

    private int startSample;
    private int peakSample;
    private int endSample;

    public QFactorCalc(double[] freq, double[] data) {
        this.freq = freq;
        this.data = data;
        this.slopeFinder = new SlopeFinder(this.data);
        this.localExtremaFinder = new LocalExtremaFinder(data);
    }

    public boolean checkBandStop(double minDepth) {
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

    public boolean checkBandPass(double minHeight) {
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

    public double getBandStart() {
        return freq[startSample];
    }

    public double getBandPeak() {
        return freq[peakSample];
    }

    public double getBandEnd() {
        return freq[endSample];
    }

    public double getBandwidth() {
        return getBandEnd() - getBandStart();
    }

    public double getQFactor() {
        return getBandPeak() / getBandwidth();
    }
}
