package com.mindpart.science;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
public class Frequency implements Comparable<Frequency> {
    public static final Unit UNIT = new Unit("value");

    public final int value;

    public Frequency(double value, UnitPrefix unitPrefix) {
        this.value = (int) unitPrefix.toBase(value);
    }

    public Frequency(int value) {
        this.value = value;
    }

    public double to(UnitPrefix unitPrefix) {
        return unitPrefix.fromBase(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Frequency frequency = (Frequency) o;

        return value == frequency.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public int compareTo(Frequency frequency) {
        return Long.compare(value, frequency.value);
    }
}