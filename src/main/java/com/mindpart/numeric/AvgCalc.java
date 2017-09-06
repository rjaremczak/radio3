package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class AvgCalc {
    private double accumulator;
    private int items;

    public AvgCalc() {
        reset();
    }
    
    public void reset() {
        items = 0;
        accumulator = 0;
    }

    public void record(double value) {
        accumulator += value;
        items++;
    }

    public boolean isReady() {
        return items > 0;
    }

    public double getAvg() {
        return accumulator / items;
    }

    public int getItems() {
        return items;
    }
}
