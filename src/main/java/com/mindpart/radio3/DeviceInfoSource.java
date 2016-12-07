package com.mindpart.radio3;

import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.utils.BinaryIterator;

import static com.mindpart.radio3.device.FrameCommand.DEVICE_GET_INFO;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfoSource implements FrameParser<DeviceInfo> {
    static final Frame GET = new Frame(DEVICE_GET_INFO);

    private DeviceService deviceService;

    public DeviceInfoSource(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == DEVICE_GET_INFO;
    }

    @Override
    public DeviceInfo parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        DeviceInfo di = new DeviceInfo();
        di.name = bi.nextString(16);
        di.buildId = bi.nextString(32);
        di.vfo.type = DeviceInfo.Vfo.Type.values()[bi.nextUInt8()];
        di.vfo.minFrequency = bi.nextUInt32();
        di.vfo.maxFrequency = bi.nextUInt32();
        return di;
    }

    public void requestData() {
        deviceService.performRequest(DeviceInfoSource.GET);
    }
}