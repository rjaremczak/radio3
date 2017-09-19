package com.mindpart.numeric;

import java.util.List;

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

    public boolean findBandStop(double minDepth) {
        for(int minimum : localExtremaFinder.getMinimaFromLowest()) {
            if(checkBandStop(minimum, minDepth)) return true;
        }
        return false;
    }

    private boolean checkBandStop(int peak, double minDepth) {
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

    public boolean findBandPass(double minDepth) {
        for(int maximum : localExtremaFinder.getMaximaFromHighest()) {
            if(checkBandPass(maximum, minDepth)) return true;
        }
        return false;
    }

    private boolean checkBandPass(int peak, double minHeight) {
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
