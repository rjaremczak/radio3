package com.mindpart.radio3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.mindpart.radio3.device.Adc;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class Configuration {
    public Adc linearAdc;
    public Adc logarithmicAdc;
    public Adc vnaGainAdc;
    public Adc vnaPhaseAdc;
    public List<SweepProfile> sweepProfiles;
}
