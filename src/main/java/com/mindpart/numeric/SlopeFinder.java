package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.02
 */
public class SlopeFinder {
    private int sample0;
    private int sample1;
    private final double[] values;

    public SlopeFinder(double[] data) {
        this.values = data;
    }

    public boolean findFallingForward(int start, double threshold) {
        for(sample1 = start; sample1 < values.length; sample1++) {
            if(values[sample1] <= threshold) {
                sample0 = Math.max(start, sample1 - 1);
                return true;
            }
        }
        return false;
    }

    public boolean findFallingBackward(int start, double threshold) {
        for(sample1 =start; sample1 >=0; sample1--) {
            if(values[sample1] <= threshold) {
                sample0 = Math.min(start, sample1 + 1);
                return true;
            }
        }
        return false;
    }

    public boolean findRisingForward(int start, double threshold) {
        for(sample1 =start; sample1 < values.length; sample1++) {
            if(values[sample1] >= threshold) {
                sample0 = Math.max(start, sample1 - 1);
                return true;
            }
        }
        return false;
    }

    public boolean findRisingBackward(int start, double threshold) {
        for(sample1 =start; sample1 >=0; sample1--) {
            if(values[sample1] >= threshold) {
                sample0 = Math.min(start, sample1 + 1);
                return true;
            }
        }
        return false;
    }

    public int getSampleNumber() {
        return sample1;
    }

    public double linearInterpolation(double[] arguments, double threshold) {
        double value0 = values[sample0];
        if(sample0==sample1) return value0;

        double value1 = values[sample1];
        double prop = (threshold - value0) / (value1 - value0);
        return (arguments[sample1] - arguments[sample0]) * prop + arguments[sample0];
    }
}
