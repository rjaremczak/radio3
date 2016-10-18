package com.mindpart.radio3.device;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.23
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class Adc {
    int adcBase;
    double base;
    double multiplier;

    public Adc() {
    }

    public Adc(int adcValueMin, int adcValueMax, double valueMin, double valueMax) {
        this.adcBase = adcValueMin;
        this.base = valueMin;
        multiplier = (valueMax - valueMin) / (adcValueMax - adcValueMin);
    }

    public double convert(int adcValue) {
        return base + ((adcValue - adcBase) * multiplier);
    }

    public static Adc getDefault() {
        return new Adc(0, 4035, 0.0, 3.45);
    }
}
