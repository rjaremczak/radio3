package com.mindpart.math;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
abstract class FunctionScanner<X, Y> {
    protected X foundX;
    protected Y foundY;

    public FunctionScanner() {
        reset();
    }

    public void reset() {
        foundX = null;
        foundY = null;
    }

    public boolean isFound() {
        return foundX !=null && foundY !=null;
    }

    public abstract void record(X argument, Y value);

    public X getFoundX() {
        return foundX;
    }

    public Y getFoundY() {
        return foundY;
    }
}
