package com.mindpart.math;

/**
 * Created by Robert Jaremczak
 * Date: 2017.05.18
 */
public final class MathUtils {
    private MathUtils() {}

    public static double floor(double value, double grid) {
        return Math.floor(value/grid) * grid;
    }

    public static double ceil(double value, double grid) {
        return Math.ceil(value/grid) * grid;
    }

    public static double tickUnit(double range, double numTicks) {
        double pow = Math.ceil(Math.log10(range/numTicks));
        double tu = Math.pow(10,pow);
        double num = Math.floor(range/tu);
        if(num < numTicks) {
            return tu/2;
        } else if(num < numTicks/2) {
            return tu/5;
        } else {
            return tu;
        }
    }
}
