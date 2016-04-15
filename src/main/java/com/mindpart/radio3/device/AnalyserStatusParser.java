package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class AnalyserStatusParser implements FrameParser<AnalyserStatus> {

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.ANALYSER_STATUS && frame.getPayloadSize() == 1;
    }

    @Override
    public AnalyserStatus parse(Frame frame) {
        return new AnalyserStatus(AnalyserStatus.State.values()[Binary.toUInt8(frame.getPayload())]);
    }
}
