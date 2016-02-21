package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    public enum VfoType { NONE, AD9850}
    public enum FrequencyMeter { NONE, STM32}

    private int firmwareVersion;
    private int hardwareVersion;
    private VfoType vfoType;
    private FrequencyMeter frequencyMeter;

    public DeviceInfo(int firmwareVersion, int hardwareVersion, int ddsCode, int frequencyMeterCode) {
        this.firmwareVersion = firmwareVersion;
        this.hardwareVersion = hardwareVersion;
        this.vfoType = VfoType.values()[ddsCode];
        this.frequencyMeter = FrequencyMeter.values()[frequencyMeterCode];
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

    public FrequencyMeter getFrequencyMeter() {
        return frequencyMeter;
    }
}
