package com.mindpart.discrete;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.27
 */
public class Sample {
    private static final NumberFormat FORMAT = new DecimalFormat("00000");

    private int number;
    private double value;

    public Sample(int number, double value) {
        this.number = number;
        this.value = value;
    }

    public int getNumber() {
        return number;
    }

    public double getValue() {
        return value;
    }

    public String toString() {
        return "["+FORMAT.format(number)+"] "+value;
    }
}
