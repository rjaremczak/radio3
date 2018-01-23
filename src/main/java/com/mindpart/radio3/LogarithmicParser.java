package com.mindpart.radio3;

import com.mindpart.radio3.device.Adc;
import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.FrameParser;
import com.mindpart.bin.Binary;

import static com.mindpart.radio3.device.FrameCommand.LOGPROBE_DATA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.09.23
 */
public class LogarithmicParser implements FrameParser<Double> {
    private static final double REF_DBM = -87;
    private static final double V_TO_DBM_RATIO = 0.025;

    private Adc adc = Adc.getDefault();

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == LOGPROBE_DATA;
    }

    public Double parse(int adc) {
        return REF_DBM + this.adc.convert(adc) / V_TO_DBM_RATIO;
    }

    @Override
    public Double parse(Frame frame) {
        return parse(Binary.toUInt16(frame.getPayload()));
    }
}
