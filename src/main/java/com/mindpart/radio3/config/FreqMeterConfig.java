package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class FreqMeterConfig {
    int multiplier;
    int base;

    public int getMultiplier() {
        return multiplier;
    }

    public int getBase() {
        return base;
    }

    public static FreqMeterConfig defaults() {
        FreqMeterConfig config = new FreqMeterConfig();
        config.base = 0;
        config.multiplier = 16;
        return config;
    }
}
