package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.23
 */
public enum FrameCmd {
    PING(0x000),
    GET_DEVICE_CONFIGURATION(0x001),
    GET_DEVICE_STATE(0x002),
    GET_VFO_FREQ(0x003),
    SET_VFO_FREQ(0x004),
    GET_ALL_PROBES(0x005),
    SET_VFO_TO_SOCKET(0x006),
    SET_VFO_TO_VNA(0x007),
    SET_VFO_TYPE(0x008),
    SET_ATTENUATOR(0x009),
    SET_AMPLIFIER(0x00a),

    SWEEP_REQUEST(0x020),
    SWEEP_RESPONSE(0x021);

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
