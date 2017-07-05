package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mindpart.radio3.device.HardwareRevision;
import com.mindpart.radio3.device.VfoType;
import org.apache.log4j.Level;

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
    private String languageTag;
    private HardwareRevision hardwareRevision = HardwareRevision.AUTODETECT;
    private VfoType vfoType = VfoType.NONE;
    private FMeterConfig fMeter;

    public Locale getLocale() {
        return languageTag==null ? Locale.getDefault() : Locale.forLanguageTag(languageTag);
    }

    public HardwareRevision getHardwareRevision() {
        return hardwareRevision;
    }

    public VfoType getVfoType() {
        return vfoType;
    }

    public FMeterConfig getFMeter() {
        return fMeter;
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

    public String getLanguageTag() {
        return languageTag;
    }
}
