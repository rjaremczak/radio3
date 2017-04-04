package com.mindpart.types;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.11
 */
public class Voltage {
    private static final double VALUE_V = 1;
    private static final double VALUE_mV = 0.001;
    private static final NumberFormat FORMAT_V = new DecimalFormat("0.### V");
    private static final NumberFormat FORMAT_mV = new DecimalFormat("0.### mV");

    private double volt;

    private Voltage(double volt) {
        this.volt = volt;
    }

    public double toMilliVolt() {
        return volt / VALUE_mV;
    }

    public double toVolt() {
        return volt;
    }

    public String format() {
        if(volt >= VALUE_V) {
            return FORMAT_V.format(volt);
        } else {
            return FORMAT_mV.format(toMilliVolt());
        }
    }

    public static Voltage ofVolt(double v) {
        return new Voltage(v);
    }

    public static Voltage ofMilliVolt(double mV) {
        return new Voltage(mV * VALUE_mV);
    }

    public static Voltage parse(String str) {
        String norm = str.trim();
        ParsePosition pos = new ParsePosition(0);

        Number value = FORMAT_mV.parse(norm, pos);
        if(value!=null) { return ofMilliVolt(value.doubleValue()); }

        value = FORMAT_V.parse(norm, pos);
        return ofVolt(value!=null ? value.doubleValue() : Double.parseDouble(norm));
    }

    @Override
    public String toString() {
        return format();
    }
}
