package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.device.HardwareRevision;
import com.mindpart.radio3.device.VfoType;
import org.apache.log4j.Level;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {
    private Level logLevel;
    private HardwareRevision hardwareRevision = HardwareRevision.AUTODETECT;
    private VfoType vfoType = VfoType.NONE;
    private FMeterConfig fMeter;
    private LinearProbeConfig linearProbe;
    private LogarithmicProbeConfig logarithmicProbe;
    private VnaConfig vna;
    public List<SweepProfile> sweepProfiles;

    public Level getLogLevel() {
        return logLevel;
    }

    public HardwareRevision getHardwareRevision() {
        return hardwareRevision;
    }

    public VfoType getVfoType() {
        return vfoType;
    }

    public FMeterConfig getfMeter() {
        return fMeter;
    }

    public LinearProbeConfig getLinearProbe() {
        return linearProbe;
    }

    public LogarithmicProbeConfig getLogarithmicProbe() {
        return logarithmicProbe;
    }

    public VnaConfig getVna() {
        return vna;
    }

    public List<SweepProfile> getSweepProfiles() {
        return sweepProfiles;
    }

    public void setVfoType(VfoType vfoType) {
        this.vfoType = vfoType;
    }

    public void setHardwareRevision(HardwareRevision hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }
}
