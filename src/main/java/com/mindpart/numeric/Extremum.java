package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class Extremum {
    enum Type { MINIMUM, MAXIMUM; }

    private int number;
    private Type type;

    public Extremum(int number, Type type) {
        this.number = number;
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Extremum extremum = (Extremum) o;

        if (number != extremum.number) return false;
        return type == extremum.type;
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return type+" "+number;
    }
}
