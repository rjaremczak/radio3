package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    public enum VfoType { NONE, AD9850}
    public enum FrequencyMeterType { NONE, STM32}

    private long timestamp;
    private int firmwareVersion;
    private int hardwareVersion;
    private VfoType vfoType;
    private FrequencyMeterType frequencyMeterType;

    public DeviceInfo(long timestamp, int firmwareVersion, int hardwareVersion, int ddsCode, int frequencyMeterCode) {
        this.timestamp = timestamp;
        this.firmwareVersion = firmwareVersion;
        this.hardwareVersion = hardwareVersion;
        this.vfoType = VfoType.values()[ddsCode];
        this.frequencyMeterType = FrequencyMeterType.values()[frequencyMeterCode];
    }

    private String formatVersion(int version) {
        return String.format("%X.%02X", Binary.toUInt8high(version), Binary.toUInt8low(version));
    }

    public String getFirmwareVersionStr() {
        return formatVersion(firmwareVersion);
    }

    public String getHardwareVersionStr() {
        return formatVersion(hardwareVersion);
    }

    public VfoType getVfoType() {
        return vfoType;
    }

    public FrequencyMeterType getFrequencyMeterType() {
        return frequencyMeterType;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
