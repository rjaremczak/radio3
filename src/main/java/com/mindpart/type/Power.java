package com.mindpart.type;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.11
 */
public class Power {
    private static final double VALUE_W = 1;
    private static final double VALUE_mW = 0.001;
    private static final NumberFormat FORMAT_W = new DecimalFormat("0.000 W");
    private static final NumberFormat FORMAT_mW = new DecimalFormat("0.0 mW");
    private static final NumberFormat FORMAT_dBm = new DecimalFormat("0.00 dBm");

    private double watt;

    private Power(double watt) {
        this.watt = watt;
    }

    public double toWatt() {
        return watt;
    }

    public double toMilliWatt() {
        return watt / VALUE_mW;
    }

    public double toDBm() {
        return 10 * Math.log10(watt) + 30;
    }

    public String format() {
        if(watt >= VALUE_W) {
            return FORMAT_W.format(toWatt());
        } else {
            return FORMAT_mW.format(toMilliWatt());
        }
    }

    public String formatDBm() {
        return FORMAT_dBm.format(toDBm());
    }

    public static Power ofWatt(double watt) {
        return new Power(watt);
    }

    public static Power ofMilliWatt(double mW) {
        return new Power(mW * VALUE_mW);
    }

    public static Power ofDBm(double dBm) {
        return new Power(Math.pow(10, (dBm - 30)/10 ));
    }

    public static Power parse(String str) {
        String norm = str.trim();
        ParsePosition pos = new ParsePosition(0);

        Number value = FORMAT_mW.parse(norm, pos);
        if(value!=null) { return ofMilliWatt(value.doubleValue()); }

        value = FORMAT_dBm.parse(norm, pos);
        if(value!=null) { return ofDBm(value.doubleValue()); }

        value = FORMAT_W.parse(norm, pos);
        return ofWatt(value!=null ? value.doubleValue() : Double.parseDouble(norm));
    }
}
