package com.mindpart.science;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
public abstract class Quantity implements Comparable<Quantity> {
    private final double value;

    public Quantity(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Quantity q) {
        return Double.compare(value, q.value);
    }

    public abstract Unit getUnit();
}
