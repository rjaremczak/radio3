package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.bin.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.19
 */
public class SweepResponseParser implements FrameParser<SweepResponse> {

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.SWEEP_RESPONSE && frame.getPayloadSize() >= 12;
    }

    @Override
    public SweepResponse parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        SweepResponse response = new SweepResponse(
                SweepState.values()[bi.nextUInt8()],
                bi.nextUInt32(),
                bi.nextUInt32(),
                bi.nextUInt16(),
                SweepSignalSource.values()[bi.nextUInt8()]);

        int data[][] = response.getData();
        for(int step=0; step<=response.getNumSteps(); step++) {
            for(int series=0; series<response.getNumSeries(); series++) {
                data[series][step] = bi.nextUInt16();
            }
        }
        return response;
    }
}
