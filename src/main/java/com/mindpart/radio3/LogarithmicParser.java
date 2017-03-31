package com.mindpart.radio3;

import com.mindpart.radio3.device.Adc;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.utils.Binary;

import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_DATA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.23
 */
public class LogarithmicParser implements FrameParser<Double> {
    private Adc adc = Adc.getDefault();

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LOGPROBE_DATA;
    }

    public Double parse(int adc) {
        // return -80 + Math.max(0, this.adc.convert(adc) - 0.25) / 0.025;
        return -89.7 + this.adc.convert(adc) / 0.025;
    }

    @Override
    public Double parse(Frame frame) {
        return parse(Binary.toUInt16(frame.getPayload()));
    }
}
