package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.23
 */
public enum FrameCmd {
    PING(0x000),
    GET_DEVICE_CONFIGURATION(0x001),
    GET_DEVICE_STATE(0x002),
    GET_LICENSE_DATA(0x003),

    GET_VFO_FREQ(0x010),
    SET_VFO_FREQ(0x011),
    GET_ALL_PROBES(0x012),
    SET_VFO_TO_SOCKET(0x013),
    SET_VFO_TO_VNA(0x014),
    SET_VFO_TYPE(0x015),
    SET_ATTENUATOR(0x016),
    SET_AMPLIFIER(0x017),

    SWEEP_REQUEST(0x030),
    SWEEP_RESPONSE(0x031);

    private int code;

    FrameCmd(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FrameCmd fromCode(int code) {
        for (FrameCmd frameCmd : values()) {
            if (frameCmd.code == code) {
                return frameCmd;
            }
        }
        throw new IllegalArgumentException(String.format("unknown frame command code 0x%04X", code));
    }
}
