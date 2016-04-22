package com.mindpart.radio3.device;

import com.mindpart.utils.BinaryIterator;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.19
 */
public class AnalyserDataParser implements FrameParser<AnalyserData> {

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.ANALYSER_DATA && frame.getPayloadSize() >= 12;
    }

    @Override
    public AnalyserData parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        AnalyserData ad = new AnalyserData(bi.nextUInt32(), bi.nextUInt32(), bi.nextUInt16(), bi.nextUInt16());
        int data[][] = ad.getData();
        for(int step=0; step<=ad.getNumSteps(); step++) {
            for(int series=0; series<ad.getNumSeries(); series++) {
                data[series][step] = bi.nextUInt16();
            }
        }
        return ad;
    }
}
