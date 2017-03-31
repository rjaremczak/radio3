package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.19
 */
public class AnalyserResponseParser implements FrameParser<AnalyserResponse> {

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.ANALYSER_RESPONSE && frame.getPayloadSize() >= 12;
    }

    @Override
    public AnalyserResponse parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        AnalyserResponse response = new AnalyserResponse(
                AnalyserState.values()[bi.nextUInt8()],
                bi.nextUInt32(),
                bi.nextUInt32(),
                bi.nextUInt16(),
                AnalyserDataSource.values()[bi.nextUInt8()]);

        int data[][] = response.getData();
        for(int step=0; step<=response.getNumSteps(); step++) {
            for(int series=0; series<response.getNumSeries(); series++) {
                data[series][step] = bi.nextUInt16();
            }
        }
        return response;
    }
}
