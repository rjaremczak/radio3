package com.mindpart.discrete;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class Extremum {
    enum Type { MINIMUM, MAXIMUM; }

    private int number;
    private double value;
    private Type type;

    public Extremum(int number, double value, Type type) {
        this.number = number;
        this.value = value;
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public double getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
}
