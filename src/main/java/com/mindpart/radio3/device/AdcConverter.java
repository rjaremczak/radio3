package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.23
 */
public class AdcConverter {
    private int adcValueMin;
    private double valueMin;
    private double adcToValueMultiplier;

    public AdcConverter(int rawValueMin, int rawValueMax, double valueMin, double valueMax) {
        this.adcValueMin = rawValueMin;
        this.valueMin = valueMin;
        adcToValueMultiplier = (valueMax - valueMin) / (rawValueMax - rawValueMin);
    }

    public double convert(int adcValue) {
        return valueMin + ((adcValue - adcValueMin) * adcToValueMultiplier);
    }

    public static AdcConverter getDefault() {
        return new AdcConverter(0, 4035, 0.0, 3.45);
    }
}
