package com.mindpart.discrete;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.02
 */
public class SlopeFinder {
    private int sampleNumber;
    private final double[] data;

    public SlopeFinder(double[] data) {
        this.data = data;
    }

    public boolean findFallingForward(int start, double threshold) {
        for(sampleNumber =start; sampleNumber <data.length; sampleNumber++) {
            if(data[sampleNumber] <= threshold) return true;
        }
        return false;
    }

    public boolean findFallingBackward(int start, double threshold) {
        for(sampleNumber =start; sampleNumber >=0; sampleNumber--) {
            if(data[sampleNumber] <= threshold) return true;
        }
        return false;
    }

    public boolean findRisingForward(int start, double threshold) {
        for(sampleNumber =start; sampleNumber <data.length; sampleNumber++) {
            if(data[sampleNumber] >= threshold) return true;
        }
        return false;
    }

    public boolean findRisingBackward(int start, double threshold) {
        for(sampleNumber =start; sampleNumber >=0; sampleNumber--) {
            if(data[sampleNumber] >= threshold) return true;
        }
        return false;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }
}
