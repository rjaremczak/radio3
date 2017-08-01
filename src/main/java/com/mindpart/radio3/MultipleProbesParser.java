package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCommand.PROBES_DATA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.09
 */
public class MultipleProbesParser implements FrameParser<ProbesValues> {
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private VnaParser vnaParser;
    private FreqMeterParser freqMeterParser;

    public MultipleProbesParser(LogarithmicParser logarithmicParser, LinearParser linearParser, VnaParser vnaParser, FreqMeterParser freqMeterParser) {
        this.logarithmicParser = logarithmicParser;
        this.linearParser = linearParser;
        this.vnaParser = vnaParser;
        this.freqMeterParser = freqMeterParser;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == PROBES_DATA;
    }

    ProbesValues parse(int logarithmic, int linear, int complexGain, int complexPhase, long fMeter) {
        return new ProbesValues(
                logarithmicParser.parse(logarithmic),
                linearParser.parse(linear),
                vnaParser.calculateVnaResult(complexGain, complexPhase),
                freqMeterParser.parse(fMeter)
        );
    }

    @Override
    public ProbesValues parse(Frame frame) {
        byte[] payload = frame.getPayload();
        return parse(Binary.toUInt16(payload, 0),
                Binary.toUInt16(payload, 2),
                Binary.toUInt16(payload, 4),
                Binary.toUInt16(payload, 6),
                Binary.toUInt32(payload, 8)
        );
    }
}
