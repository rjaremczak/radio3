package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.PROBES_GET;
import static com.mindpart.radio3.device.FrameCommand.PROBES_STOP_SAMPLING;
import static com.mindpart.radio3.device.FrameCommand.PROBES_START_SAMPLING;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class MultipleProbes implements FrameParser<ProbeValues> {
    static final Frame SAMPLE = new Frame(PROBES_GET);
    static final Frame START_SAMPLING = new Frame(PROBES_START_SAMPLING);
    static final Frame STOP_SAMPLING = new Frame(PROBES_STOP_SAMPLING);

    private DeviceService deviceService;
    private LogarithmicProbe logarithmicProbe;
    private LinearProbe linearProbe;
    private VnaProbe vnaProbe;
    private FMeterProbe fMeterProbe;

    public MultipleProbes(DeviceService deviceService, LogarithmicProbe logarithmicProbe, LinearProbe linearProbe, VnaProbe vnaProbe, FMeterProbe fMeterProbe) {
        this.deviceService = deviceService;
        this.logarithmicProbe = logarithmicProbe;
        this.linearProbe = linearProbe;
        this.vnaProbe = vnaProbe;
        this.fMeterProbe = fMeterProbe;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == PROBES_GET;
    }

    ProbeValues parse(int logarithmic, int linear, int complexGain, int complexPhase, long fMeter) {
        return new ProbeValues(
                logarithmicProbe.parse(logarithmic),
                linearProbe.parse(linear),
                vnaProbe.parse(complexGain, complexPhase),
                fMeterProbe.parse(fMeter)
        );
    }

    @Override
    public ProbeValues parse(Frame frame) {
        byte[] payload = frame.getPayload();
        return parse(Binary.toUInt16(payload, 0),
                Binary.toUInt16(payload, 2),
                Binary.toUInt16(payload, 4),
                Binary.toUInt16(payload, 6),
                Binary.toUInt32(payload, 8)
        );
    }

    public void requestData() {
        deviceService.performRequest(MultipleProbes.SAMPLE);
    }

    public void startSampling() {
        deviceService.performRequest(MultipleProbes.START_SAMPLING);
    }

    public void stopSampling() {
        deviceService.performRequest(MultipleProbes.STOP_SAMPLING);
    }
}
