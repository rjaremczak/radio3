package com.mindpart.types;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.09
 */
public class SWR {
    private static final NumberFormat DEF_FORMAT = new DecimalFormat("0.##");

    private double value;

    public SWR(double value) {
        this.value = value;
    }

    public void parse(String str) {
        value = Double.parseDouble(str);
    }

    public String format() {
        return DEF_FORMAT.format(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return format();
    }
}
