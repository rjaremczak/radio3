package com.mindpart.radio3;

import com.mindpart.radio3.config.FreqMeterConfig;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.type.Frequency;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCommand.FMETER_DATA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class FreqMeterParser implements FrameParser<Frequency> {
    private FreqMeterConfig config;

    public FreqMeterParser(FreqMeterConfig config) {
        this.config = config;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FMETER_DATA && frame.getPayloadSize() == 4;
    }

    @Override
    public Frequency parse(Frame frame) {
        return parse(Binary.toUInt32(frame.getPayload()));
    }

    Frequency parse(long count) {
        return Frequency.ofHz((count * config.multiplier) + config.base);
    }
}
