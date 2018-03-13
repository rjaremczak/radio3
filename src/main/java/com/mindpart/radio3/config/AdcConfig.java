package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.mindpart.radio3.device.Adc;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class AdcConfig {
    int adcMin;
    int adcMax;
    double valueMin;
    double valueMax;

    public Adc createAdc() {
        return new Adc(adcMin, adcMax, valueMin, valueMax);
    }

    public static final AdcConfig defaults() {
        AdcConfig adcConfig = new AdcConfig();
        adcConfig.adcMin = 0;
        adcConfig.adcMax = 4095;
        adcConfig.valueMin = 0.0;
        adcConfig.valueMax = 3.3;
        return adcConfig;
    }
}
