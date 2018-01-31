package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum HardwareRevision {
    AUTODETECT(0, "Auto", false, false),
    VERSION_1(1, false, false),
    VERSION_2(2, true, true),
    VERSION_3(3, true, true);

    private int code;
    private String name;
    private boolean attenuator;
    private boolean amplifier;

    HardwareRevision(int code, String name, boolean attenuator, boolean amplifier) {
        this.code = code;
        this.name = name;
        this.attenuator = attenuator;
        this.amplifier = amplifier;
    }

    HardwareRevision(int code, boolean attenuator, boolean amplifier) {
        this(code, "Ver. " + code, attenuator, amplifier);
    }

    public boolean isAttenuator() {
        return attenuator;
    }

    public boolean isAmplifier() {
        return amplifier;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
