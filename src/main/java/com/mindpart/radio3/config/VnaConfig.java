package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.30
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class VnaConfig {
    AdcConfig gainAdc;
    AdcConfig phaseAdc;
}