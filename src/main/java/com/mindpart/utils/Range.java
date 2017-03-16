package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.06
 */
public class Range {
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    public double update(double v) {
        min = Math.min(min, v);
        max = Math.max(max, v);
        return v;
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

    public boolean isValid() {
        return span() < Double.MAX_VALUE;
    }
}
