package com.mindpart.radio3;

import com.mindpart.radio3.device.*;
import com.mindpart.radio3.ui.SweepQuality;
import com.mindpart.utils.BinaryIterator;

import java.util.function.Consumer;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.19
 */
public class Sweeper implements FrameParser<AnalyserData> {
    private DeviceService deviceService;

    public Sweeper(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCommand.ANALYSER_DATA && frame.getPayloadSize() >= 12;
    }

    @Override
    public AnalyserData parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        AnalyserData ad = new AnalyserData(
                AnalyserState.values()[bi.nextUInt8()],
                bi.nextUInt32(),
                bi.nextUInt32(),
                bi.nextUInt16(),
                AnalyserDataSource.values()[bi.nextUInt8()]);

        int data[][] = ad.getData();
        for(int step=0; step<=ad.getNumSteps(); step++) {
            for(int series=0; series<ad.getNumSeries(); series++) {
                data[series][step] = bi.nextUInt16();
            }
        }
        return ad;
    }
}
