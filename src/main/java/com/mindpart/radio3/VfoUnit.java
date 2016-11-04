package com.mindpart.radio3;

import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.types.Frequency;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.VFO_GET_FREQ;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class VfoUnit implements FrameParser<Frequency> {
    static final Frame SAMPLE = new Frame(VFO_GET_FREQ);

    private DeviceService deviceService;

    public VfoUnit(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == VFO_GET_FREQ && frame.getPayloadSize() == 4;
    }

    @Override
    public Frequency parse(Frame frame) {
        return Frequency.ofHz(Binary.toUInt32(frame.getPayload()));
    }

    public void requestData() {
        deviceService.performRequest(VfoUnit.SAMPLE);
    }
}
