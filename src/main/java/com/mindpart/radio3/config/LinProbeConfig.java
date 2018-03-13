package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class LinProbeConfig {
    AdcConfig adcConfig = new AdcConfig();
    double baseVrms;
    double ratioVToVrms;

    public AdcConfig getAdcConfig() {
        return adcConfig;
    }

    public double getBaseVrms() {
        return baseVrms;
    }

    public double getRatioVToVrms() {
        return ratioVToVrms;
    }

    public static LinProbeConfig defaults() {
        LinProbeConfig config = new LinProbeConfig();
        config.adcConfig = AdcConfig.defaults();
        config.baseVrms = -0.0265;
        config.ratioVToVrms = 7.5 / 1.80;
        return config;
    }
}
