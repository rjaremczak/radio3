package com.mindpart.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class ExtremaFinder<X, Y extends Comparable<Y>> {
    private List<Extremum<X, Y>> extrema = new ArrayList<>();

    public ExtremaFinder() {
        reset();
    }

    public void reset() {
        extrema.clear();
    }

    public void record(X x, Y y) {

    }

    public List<Extremum<X, Y>> getExtrema() {
        return extrema;
    }
}
