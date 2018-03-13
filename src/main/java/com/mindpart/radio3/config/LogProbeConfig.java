package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class LogProbeConfig {
    AdcConfig adcConfig = new AdcConfig();
    double baseDbm = -87;
    double ratioVToDbm = 0.025;

    public AdcConfig getAdcConfig() {
        return adcConfig;
    }

    public double getBaseDbm() {
        return baseDbm;
    }

    public double getRatioVToDbm() {
        return ratioVToDbm;
    }

    public static LogProbeConfig defaults() {
        LogProbeConfig config = new LogProbeConfig();
        config.adcConfig = AdcConfig.defaults();
        config.baseDbm = -87;
        config.ratioVToDbm = 0.025;
        return config;
    }
}
