package com.mindpart.numeric;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.06
 */
public class LocalExtremaFinder {
    private final double[] data;
    private final double[] derivative;
    private final List<Integer> minima = new ArrayList<>();
    private final List<Integer> maxima = new ArrayList<>();

    public LocalExtremaFinder(double[] data) {
        this.data = data;
        this.derivative = Analysis.differentiate(data);
        scan();
    }

    private void scan() {
        for(int i = 1; i<derivative.length; i++) {
            boolean prev = derivative[i-1] >= 0;
            boolean curr = derivative[i] >= 0;
            if(prev && !curr) {
                maxima.add(i);
            } else if(!prev && curr) {
                minima.add(i);
            }
        }

        minima.sort(Comparator.comparingDouble(s -> data[s]));
        maxima.sort(Comparator.comparingDouble(s -> data[s]));
    }

    public List<Integer> getMinima() {
        return minima;
    }

    public List<Integer> getMaxima() {
        return maxima;
    }

    public List<Integer> getMinimaFromLowest() {
        return minima.stream().sorted(Comparator.comparingDouble(s -> data[s])).collect(Collectors.toList());
    }

    public List<Integer> getMaximaFromHighest() {
        return maxima.stream().sorted(Comparator.comparingDouble(s -> -data[s])).collect(Collectors.toList());
    }

    public double[] getDerivative() {
        return derivative;
    }
}
