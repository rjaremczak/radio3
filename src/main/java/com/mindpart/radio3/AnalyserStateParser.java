package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class AnalyserStateParser implements FrameParser<AnalyserState> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.ANALYSER_STATE && frame.getPayloadSize() == 1;
    }

    @Override
    public AnalyserState parse(Frame frame) {
        return AnalyserState.values()[Binary.toUInt8(frame.getPayload())];
    }
}
