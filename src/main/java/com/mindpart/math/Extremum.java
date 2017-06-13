package com.mindpart.math;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class Extremum<X,Y> {
    private X x;
    private Y y;
    private boolean maximum;

    public Extremum(X x, Y y, boolean maximum) {
        this.x = x;
        this.y = y;
        this.maximum = maximum;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    public boolean isMaximum() {
        return maximum;
    }
}
