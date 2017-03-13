package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.23
 */

public class Adc {
    private int adcBase;
    private double base;
    private double multiplier;

    public Adc(int adcValueMin, int adcValueMax, double valueMin, double valueMax) {
        this.adcBase = adcValueMin;
        this.base = valueMin;
        this.multiplier = (valueMax - valueMin) / (adcValueMax - adcValueMin);
    }

    public double convert(int adcValue) {
        return base + ((adcValue - adcBase) * multiplier);
    }

    public static Adc getDefault() {
        return new Adc(0, 4095, 0.0, 3.3);
    }
}
