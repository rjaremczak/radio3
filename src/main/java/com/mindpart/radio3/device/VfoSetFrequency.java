package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.21
 */
class VfoSetFrequency extends Frame {

    VfoSetFrequency(int frequency) {
        super(FrameCommand.VFO_SET_FREQ, Binary.fromUInt32(frequency));
    }
}
