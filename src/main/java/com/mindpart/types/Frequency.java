package com.mindpart.types;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
public class Frequency implements Comparable<Frequency> {
    private static final double VALUE_MHZ = 1000000;
    private static final double VALUE_KHZ = 1000;
    
    private static final String SYMBOL_MHZ = "MHz";
    private static final String SYMBOL_KHZ = "kHz";
    private static final String SYMBOL_HZ = "Hz";

    public static final Frequency ZERO = Frequency.ofHz(0);

    private long valueHz;

    private Frequency(long valueHz) {
        this.valueHz = valueHz;
    }

    public double toMHz() {
        return valueHz / VALUE_MHZ;
    }

    public double toKHz() {
        return valueHz / VALUE_KHZ;
    }

    public long toHz() {
        return valueHz;
    }

    public String format() {
        if(valueHz >= VALUE_MHZ) {
            return toMHz() + " " + SYMBOL_MHZ;
        } else if(valueHz >= VALUE_KHZ) {
            return toKHz() + " " + SYMBOL_KHZ;
        } else {
            return toHz() + " " + SYMBOL_HZ;
        }
    }

    public static final Frequency ofMHz(double mhz) {
        return new Frequency(Math.round(mhz * VALUE_MHZ));
    }

    public static final Frequency ofKHz(double khz) {
        return new Frequency((long) (khz * VALUE_KHZ));
    }

    public static final Frequency ofHz(long hz) {
        return new Frequency(hz);
    }

    private static String extractValue(String str, String unitSymbol) {
        int pos = str.indexOf(unitSymbol);
        return pos>0 ? str.substring(0,pos) : null;
    }
    
    public static final Frequency parse(String str) {
        String norm = str.trim();
        String value = extractValue(norm, SYMBOL_MHZ);
        if(value != null) { return ofMHz(Double.parseDouble(value)); }

        value = extractValue(norm, SYMBOL_KHZ);
        if(value != null) { return ofKHz(Double.parseDouble(value)); }

        value = extractValue(norm, SYMBOL_HZ);
        return new Frequency((int)Double.parseDouble(value!=null ? value : norm));
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