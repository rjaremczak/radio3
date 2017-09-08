package com.mindpart.numeric;

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

    public Range() {
        this.min = Double.MAX_VALUE;
        this.max = -Double.MAX_VALUE;

    }

    public double sample(double v) {
        min = Math.min(min, v);
        max = Math.max(max, v);
        return v;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public double span() {
        return Math.abs(max - min);
    }

    public double mid() {
        return min + span()/2;
    }
    public boolean contains(double v) {
        return v >= min && v <= max;
    }

    public boolean isValid() {
        return span() < Double.MAX_VALUE;
    }

    public String toString() {
        return min+" - "+max;
    }

}
