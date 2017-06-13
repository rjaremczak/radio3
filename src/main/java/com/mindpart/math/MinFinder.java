package com.mindpart.math;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MinFinder<X,Y extends Comparable<Y>> extends FunctionScanner<X,Y> {
    @Override
    public void record(X x, Y y) {
        if(this.foundY ==null || this.foundY.compareTo(y) > 0) {
            this.foundX = x;
            this.foundY = y;
        }
    }
}
