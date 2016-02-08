package com.mindpart.radio3;

import com.mindpart.radio3.device.DeviceService;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.05
 */
public class Radio3 {
    public DeviceService deviceService;

    public Radio3() {
        deviceService = new DeviceService();
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }
}
