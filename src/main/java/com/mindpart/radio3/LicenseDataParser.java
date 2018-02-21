package com.mindpart.radio3;

import com.mindpart.bin.BinaryIterator;
import com.mindpart.radio3.device.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class LicenseDataParser implements FrameParser<LicenseData> {
    @Override
    public boolean recognizes(Frame frame) {
        return frame.getCommand() == FrameCmd.GET_LICENSE_DATA;
    }


    @Override
    public LicenseData parse(Frame frame) {
        BinaryIterator bi = frame.binaryIterator();
        LicenseData ld = new LicenseData();
        ld.uniqueId0 = bi.nextUInt32();
        ld.product = bi.nextString(24);
        ld.uniqueId1 = bi.nextUInt32();
        ld.owner = bi.nextString(24);
        ld.uniqueId2 = bi.nextUInt32();
        return ld;
    }
}
