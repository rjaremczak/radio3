package com.mindpart.radio3.device;

import com.mindpart.radio3.config.VfoConfig;

/**
 * Created by Robert Jaremczak
 * Date: 2018.02.16
 */
public class DeviceConfiguration {
    public boolean licenseOk;
    public long coreUniqueId0;
    public long coreUniqueId1;
    public long coreUniqueId2;
    public int firmwareVersionMajor;
    public int firmwareVersionMinor;
    public long firmwareBuildTimestamp;
    public HardwareRevision hardwareRevision;
    public VfoConfig.Type vfoType;
}
