package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.PROBES_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class MultipleProbesParser implements FrameParser<ProbeValues> {
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private VnaParser vnaParser;
    private FMeterParser fMeterParser;

    public MultipleProbesParser(LogarithmicParser logarithmicParser, LinearParser linearParser, VnaParser vnaParser, FMeterParser fMeterParser) {
        this.logarithmicParser = logarithmicParser;
        this.linearParser = linearParser;
        this.vnaParser = vnaParser;
        this.fMeterParser = fMeterParser;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == PROBES_GET;
    }

    ProbeValues parse(int logarithmic, int linear, int complexGain, int complexPhase, long fMeter) {
        return new ProbeValues(
                logarithmicParser.parse(logarithmic),
                linearParser.parse(linear),
                vnaParser.parse(complexGain, complexPhase),
                fMeterParser.parse(fMeter)
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
}
