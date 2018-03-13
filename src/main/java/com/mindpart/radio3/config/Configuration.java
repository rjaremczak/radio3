package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Configuration {
    @JsonProperty("locale")
    String localeStr;

    @JsonIgnore
    private Locale locale;

    @JsonProperty("vfo")
    VfoConfig vfoConfig;

    @JsonProperty("fmeter")
    FreqMeterConfig freqMeterConfig;

    @JsonProperty("log")
    LogProbeConfig logProbeConfig;

    @JsonProperty("lin")
    LinProbeConfig linProbeConfig;

    @JsonProperty("vna")
    VnaConfig vnaConfig;

    public Locale getLocale() {
        if(locale==null) {
            locale = localeStr==null ? Locale.getDefault() : Locale.forLanguageTag(localeStr);
        }
        return locale;
    }

    public void setVfoType(VfoConfig.Type vfoType) {
        vfoConfig.type = vfoType;
    }

    public VfoConfig getVfoConfig() {
        return vfoConfig;
    }

    public FreqMeterConfig getFreqMeterConfig() {
        return freqMeterConfig;
    }

    public LogProbeConfig getLogProbeConfig() {
        return logProbeConfig;
    }

    public LinProbeConfig getLinProbeConfig() {
        return linProbeConfig;
    }

    public VnaConfig getVnaConfig() {
        return vnaConfig;
    }

    public static final Configuration defaults() {
        Configuration configuration = new Configuration();
        configuration.localeStr = "pl";
        configuration.freqMeterConfig = FreqMeterConfig.defaults();
        configuration.logProbeConfig = LogProbeConfig.defaults();
        configuration.linProbeConfig = LinProbeConfig.defaults();
        configuration.vnaConfig = VnaConfig.defaults();
        configuration.vfoConfig = VfoConfig.defaults();
        return configuration;
    }
}