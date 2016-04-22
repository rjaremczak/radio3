package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    private String name;
    private String buildId;
    private String vfoName;
    private long vfoMinFrequency;
    private long vfoMaxFrequency;
    private String fMeterName;
    private long fMeterMinFrequency;
    private long fMeterMaxFrequency;

    public DeviceInfo(String name, String buildId,
                      String vfoName, long vfoMinFrequency, long vfoMaxFrequency,
                      String fMeterName, long fMeterMinFrequency, long fMeterMaxFrequency) {
        this.name = name;
        this.buildId = buildId;
        this.vfoName = vfoName;
        this.vfoMinFrequency = vfoMinFrequency;
        this.vfoMaxFrequency = vfoMaxFrequency;
        this.fMeterName = fMeterName;
        this.fMeterMinFrequency = fMeterMinFrequency;
        this.fMeterMaxFrequency = fMeterMaxFrequency;
    }

    public String getName() {
        return name;
    }

    public String getBuildId() {
        return buildId;
    }

    public String getVfoName() {
        return vfoName;
    }

    public long getVfoMinFrequency() {
        return vfoMinFrequency;
    }

    public long getVfoMaxFrequency() {
        return vfoMaxFrequency;
    }

    public String getfMeterName() {
        return fMeterName;
    }

    public long getfMeterMinFrequency() {
        return fMeterMinFrequency;
    }

    public long getfMeterMaxFrequency() {
        return fMeterMaxFrequency;
    }
}
