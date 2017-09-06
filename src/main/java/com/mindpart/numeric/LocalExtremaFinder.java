package com.mindpart.numeric;

import java.util.ArrayList;
import java.util.List;

import static com.mindpart.numeric.Extremum.Type.MAXIMUM;
import static com.mindpart.numeric.Extremum.Type.MINIMUM;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.06
 */
public class LocalExtremaFinder {
    private final double[] data;
    private final double[] derivative;
    private final List<Extremum> extrema = new ArrayList<>();

    public LocalExtremaFinder(double[] data) {
        this.data = data;
        this.derivative = Analysis.differentiate(data);
        find();
    }

    private void find() {
        for(int i = 1; i<derivative.length; i++) {
            boolean prev = derivative[i-1] >= 0;
            boolean curr = derivative[i] >= 0;
            if(prev && !curr) {
                extrema.add(new Extremum(i, MAXIMUM));
            } else if(!prev && curr) {
                extrema.add(new Extremum(i, MINIMUM));
            }
        }
    }

    public List<Extremum> getExtrema() {
        return extrema;
    }

    public Extremum getLowestMinimum() {
        Extremum found = null;
        for(Extremum ex : extrema) {
            if(ex.getType()==MINIMUM && (found==null || data[ex.getNumber()] < data[found.getNumber()])) {
                found = ex;
            }
        };
        return found;
    }

    public Extremum getHighestMaximum() {
        Extremum found = null;
        for(Extremum ex : extrema) {
            if(ex.getType()==MAXIMUM && (found==null || data[ex.getNumber()] > data[found.getNumber()])) {
                found = ex;
            }
        };
        return found;
    }

    public double[] getDerivative() {
        return derivative;
    }
}
