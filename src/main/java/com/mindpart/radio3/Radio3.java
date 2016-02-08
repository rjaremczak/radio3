package com.mindpart.radio3;

import com.mindpart.radio3.device.DeviceService;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.05
 */
public class Radio3 {
    private DeviceService deviceService;

    public Radio3() {
        deviceService = new DeviceService();
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public void shutdown() {
        if(deviceService.isConnected()) {
            deviceService.disconnect();
        }
    }
}
