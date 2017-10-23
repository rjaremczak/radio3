package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mindpart.radio3.device.HardwareRevision;
import com.mindpart.radio3.device.VfoType;

import java.util.Locale;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

    @JsonIgnore
    private Locale locale;
    private String createdBy;
    private String uiLocale;
    private boolean keepAlive = false;
    private HardwareRevision hardwareRevision = HardwareRevision.AUTODETECT;
    private VfoType vfoType = VfoType.NONE;
    private FreqMeterConfig freqMeter;

    public Locale getLocale() {
        return uiLocale ==null ? Locale.getDefault() : Locale.forLanguageTag(uiLocale);
    }

    public HardwareRevision getHardwareRevision() {
        return hardwareRevision;
    }

    public VfoType getVfoType() {
        return vfoType;
    }

    public FreqMeterConfig getFreqMeter() {
        return freqMeter;
    }

    public void setVfoType(VfoType vfoType) {
        this.vfoType = vfoType;
    }

    public void setHardwareRevision(HardwareRevision hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    protected void setCreatedBy(String str) {
        createdBy = str;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUiLocale() {
        return uiLocale;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }
}
