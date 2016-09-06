package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.06
 */
public class Range {
    private double min;
    private double max;

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double span() {
        return Math.abs(max - min);
    }

    public boolean contains(double v) {
        return v >= min && v <= max;
    }
}
