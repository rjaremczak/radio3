package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class DevicePropertiesResponse extends Frame {
    public static final Frame.Type TYPE = new Frame.Type(0x8000);

    public DevicePropertiesResponse() {
        super(TYPE);
    }

    public String getFirmwareVersion() {
        return String.format("%X.%04X", Binary.word(payload, 0), Binary.word(payload, 2));
    }
}
