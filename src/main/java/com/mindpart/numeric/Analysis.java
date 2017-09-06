package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.06
 */
public class Analysis {
    private Analysis() {
    }

    public static double[] differentiate(double[] data) {
        double[] derivative = new double[data.length];
        for(int i=1; i<data.length; i++) {
            derivative[i-1] = data[i] - data[i-1];
        }
        derivative[data.length-1] = derivative[data.length-2];
        return derivative;
    }
}
