package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class VnaConfig {
    @JsonProperty("gainAdc")
    AdcConfig gainAdcConfig = new AdcConfig();
    @JsonProperty("phaseAdc")
    AdcConfig phaseAdcConfig = new AdcConfig();

    public AdcConfig getGainAdcConfig() {
        return gainAdcConfig;
    }

    public AdcConfig getPhaseAdcConfig() {
        return phaseAdcConfig;
    }

    public static VnaConfig defaults() {
        VnaConfig config = new VnaConfig();
        config.gainAdcConfig = AdcConfig.defaults();
        config.phaseAdcConfig = AdcConfig.defaults();
        return config;
    }
}
