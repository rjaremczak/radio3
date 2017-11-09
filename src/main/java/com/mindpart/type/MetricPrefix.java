package com.mindpart.type;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
public enum MetricPrefix {
    TERA(1E12, "T"),
    GIGA(1E9, "G"),
    MEGA(1E6, "M"),
    KILO(1E3, "k"),
    BASE(1, ""),
    MILLI(1E-3, "m"),
    MICRO(1E-6, ""),
    NANO(1E-9, "n"),
    PICO(1E-12, "p");

    private final double factor;
    private final String symbol;

    MetricPrefix(double factor, String symbol) {
        this.factor = factor;
        this.symbol = symbol;
    }

    public double fromBase(double base) {
        return base / factor;
    }

    public double toBase(double v) {
        return v * factor;
    }

    public String getSymbol() {
        return symbol;
    }

    public static MetricPrefix autoRange(double value) {
        for(MetricPrefix prefix : MetricPrefix.values()) {
            if(prefix.factor <= value) return prefix;
        }
        return BASE;
    }
}
