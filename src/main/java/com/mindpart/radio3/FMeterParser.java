package com.mindpart.radio3;

import com.mindpart.radio3.config.FMeterConfig;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.types.Frequency;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.FMETER_GET;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.22
 */
public class FMeterParser implements FrameParser<Frequency> {
    private FMeterConfig config;

    public FMeterParser(FMeterConfig config) {
        this.config = config;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FMETER_GET && frame.getPayloadSize() == 4;
    }

    @Override
    public Frequency parse(Frame frame) {
        return parse(Binary.toUInt32(frame.getPayload()));
    }

    Frequency parse(long count) {
        return Frequency.ofHz((count * config.multiplier) + config.base);
    }
}
