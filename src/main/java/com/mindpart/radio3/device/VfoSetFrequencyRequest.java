package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.21
 */
class VfoSetFrequencyRequest extends Frame {
    static final int TYPE = 0x001;

    VfoSetFrequencyRequest(int frequency) {
        super(TYPE, Binary.fromUInt32(frequency));
    }
}
