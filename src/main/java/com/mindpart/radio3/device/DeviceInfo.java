package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    public enum DdsType { NONE, AD9850 }
    public enum FrequencyMeter { NONE, STM32 }

    private int firmwareVersion;
    private int hardwareVersion;
    private DdsType ddsType;
    private FrequencyMeter frequencyMeter;

    public DeviceInfo(int firmwareVersion, int hardwareVersion, int ddsCode, int frequencyMeterCode) {
        this.firmwareVersion = firmwareVersion;
        this.hardwareVersion = hardwareVersion;
        this.ddsType = DdsType.values()[ddsCode];
        this.frequencyMeter = FrequencyMeter.values()[frequencyMeterCode];
    }

    private String formatVersion(int version) {
        return String.format("%X.%02X", Binary.uint8high(version), Binary.uint8low(version));
    }

    public String getFirmwareVersionStr() {
        return formatVersion(firmwareVersion);
    }

    public String getHardwareVersionStr() {
        return formatVersion(hardwareVersion);
    }

    public DdsType getDdsType() {
        return ddsType;
    }

    public FrequencyMeter getFrequencyMeter() {
        return frequencyMeter;
    }
}
