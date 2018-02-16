package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum HardwareRevision {
    AUTODETECT(0, false, false),
    VERSION_1(1, false, false),
    VERSION_2(2, true, true),
    VERSION_3(3, true, true);

    private int code;
    private boolean attenuator;
    private boolean amplifier;

    HardwareRevision(int code, boolean attenuator, boolean amplifier) {
        this.code = code;
        this.attenuator = attenuator;
        this.amplifier = amplifier;
    }

    public boolean isAttenuator() {
        return attenuator;
    }

    public boolean isAmplifier() {
        return amplifier;
    }

    @Override
    public String toString() {
        return Integer.toString(code);
    }

    public int getCode() {
        return code;
    }
}
