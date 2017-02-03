package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.device.HardwareRevision;
import com.mindpart.radio3.device.VfoType;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class Configuration {
    public FMeterConfig fMeter;
    public LinearProbeConfig linearProbe;
    public LogarithmicProbeConfig logarithmicProbe;
    public VnaConfig vna;
    public List<SweepProfile> sweepProfiles;
    public HardwareRevision hardwareRevision;
    public VfoType vfoType;
}
