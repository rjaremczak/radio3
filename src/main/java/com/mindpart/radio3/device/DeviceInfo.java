package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    private String name;
    private int version;
    private String vfoName;
    private long vfoMinFrequency;
    private long vfoMaxFrequency;
    private String fMeterName;
    private long fMeterMinFrequency;
    private long fMeterMaxFrequency;

    public DeviceInfo(String name, int version,
                      String vfoName, long vfoMinFrequency, long vfoMaxFrequency,
                      String fMeterName, long fMeterMinFrequency, long fMeterMaxFrequency) {
        this.name = name;
        this.version = version;
        this.vfoName = vfoName;
        this.vfoMinFrequency = vfoMinFrequency;
        this.vfoMaxFrequency = vfoMaxFrequency;
        this.fMeterName = fMeterName;
        this.fMeterMinFrequency = fMeterMinFrequency;
        this.fMeterMaxFrequency = fMeterMaxFrequency;
    }

    private String formatVersion(int version) {
        return String.format("%X.%02X", Binary.toUInt8high(version), Binary.toUInt8low(version));
    }

    public String getName() {
        return name;
    }

    public String getVersionStr() {
        return formatVersion(version);
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
