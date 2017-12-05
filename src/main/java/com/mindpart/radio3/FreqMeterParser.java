package com.mindpart.radio3;

import com.mindpart.radio3.config.FreqMeterConfig;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCommand.FMETER_DATA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class FreqMeterParser implements FrameParser<Integer> {
    private FreqMeterConfig config;

    public FreqMeterParser(FreqMeterConfig config) {
        this.config = config;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FMETER_DATA && frame.getPayloadSize() == 4;
    }

    @Override
    public Integer parse(Frame frame) {
        return parse(Binary.toUInt32(frame.getPayload()));
    }

    Integer parse(long count) {
        return Math.toIntExact((count * config.multiplier) + config.base);
    }
}
