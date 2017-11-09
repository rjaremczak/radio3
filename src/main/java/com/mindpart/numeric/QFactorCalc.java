package com.mindpart.numeric;

import com.mindpart.type.Capacitance;
import com.mindpart.type.Inductance;
import com.mindpart.type.Resistance;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.04
 */
public class QFactorCalc {
    private final double[] freq;
    private final double[] data;
    private final SlopeFinder slopeFinder;
    private final LocalExtremaFinder localExtremaFinder;

    private double bandStart;
    private double bandPeak;
    private double bandEnd;

    private double omega;

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
            bandStart = slopeFinder.linearInterpolation(freq, threshold);
            if(slopeFinder.findRisingForward(peak, threshold)) {
                bandEnd = slopeFinder.linearInterpolation(freq, threshold);
                bandPeak = freq[peak];
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
            bandStart = slopeFinder.linearInterpolation(freq, threshold);
            if(slopeFinder.findFallingForward(peak, threshold)) {
                bandEnd = slopeFinder.linearInterpolation(freq, threshold);
                bandPeak = freq[peak];
                return true;
            }
        }
        return false;
    }

    public double getBandStart() {
        return bandStart;
    }

    public double getBandPeak() {
        return bandPeak;
    }

    public double getBandEnd() {
        return bandEnd;
    }

    public double getBandwidth() {
        return bandEnd - bandStart;
    }

    public double getQFactor() {
        return bandPeak / getBandwidth();
    }
}
