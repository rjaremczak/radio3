package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class DeviceStatusRequest extends Frame {
    public static final Frame.Type TYPE = new Frame.Type(0x0000);
    public static final Frame FRAME = new DeviceStatusRequest();

    private DeviceStatusRequest() {
        super(TYPE);
    }
}
