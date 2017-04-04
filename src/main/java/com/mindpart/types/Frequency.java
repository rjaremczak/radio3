package com.mindpart.types;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
public class Frequency implements Comparable<Frequency> {
    private static final double VALUE_MHz = 1000000;
    private static final double VALUE_kHz = 1000;
    private static final NumberFormat FORMAT_MHZ = new DecimalFormat("0.### MHz");
    private static final NumberFormat FORMAT_KHZ = new DecimalFormat("0.### kHz");
    private static final NumberFormat FORMAT_HZ = new DecimalFormat("# Hz");

    public static final Frequency ZERO = Frequency.ofHz(0);

    private long valueHz;

    private Frequency(long valueHz) {
        this.valueHz = valueHz;
    }

    public double toMHz() {
        return valueHz / VALUE_MHz;
    }

    public double toKHz() {
        return valueHz / VALUE_kHz;
    }

    public long toHz() {
        return valueHz;
    }

    public String format() {
        if(valueHz >= VALUE_MHz) {
            return FORMAT_MHZ.format(toMHz());
        } else if(valueHz >= VALUE_kHz) {
            return FORMAT_KHZ.format(toKHz());
        } else {
            return FORMAT_HZ.format(toHz());
        }
    }

    public static Frequency ofMHz(double mhz) {
        return new Frequency(Math.round(mhz * VALUE_MHz));
    }

    public static Frequency ofKHz(double khz) {
        return new Frequency((long) (khz * VALUE_kHz));
    }

    public static Frequency ofHz(long hz) {
        return new Frequency(hz);
    }

    public static Frequency parse(String str) {
        String norm = str.trim();
        ParsePosition pos = new ParsePosition(0);

        Number value = FORMAT_MHZ.parse(norm, pos);
        if(value!=null) { return ofMHz(value.doubleValue()); }

        value = FORMAT_KHZ.parse(norm, pos);
        if(value != null) { return ofKHz(value.doubleValue()); }

        value = FORMAT_HZ.parse(norm, pos);
        return ofHz(value!=null ? value.intValue() : Integer.parseInt(norm));
    }

    public static double toMHz(long hz) {
        return ((double) hz) / VALUE_MHz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Frequency frequency = (Frequency) o;

        return valueHz == frequency.valueHz;

    }

    @Override
    public int hashCode() {
        return (int) (valueHz ^ (valueHz >>> 32));
    }

    @Override
    public int compareTo(Frequency frequency) {
        return Long.compare(valueHz, frequency.valueHz);
    }

    public boolean inRange(Frequency min, Frequency max) {
        return valueHz>=min.valueHz && valueHz<=max.valueHz;
    }

    @Override
    public String toString() {
        return format();
    }
}