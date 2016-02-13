package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    private int firmwareVersion;

    public DeviceInfo(int firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getFirmwareVersionStr() {
        return String.format("%X.%02X", Binary.uint8high(firmwareVersion), Binary.uint8low(firmwareVersion));
    }
}
