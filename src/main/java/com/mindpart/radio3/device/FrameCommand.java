package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.23
 */
public enum FrameCommand {
    PING(0x000),
    DEVICE_GET_INFO(0x001),
    DEVICE_GET_STATE(0x002),
    DEVICE_HARDWARE_REVISION(0x003),

    VFO_GET_FREQ(0x008),
    VFO_SET_FREQ(0x009),

    LOGPROBE_DATA(0x010),

    LINPROBE_DATA(0x018),

    CMPPROBE_DATA(0x020),

    FMETER_DATA(0x028),

    PROBES_DATA(0x030),

    VFO_OUT_DIRECT(0x033),
    VFO_OUT_VNA(0x034),
    VFO_TYPE(0x035),
    VFO_ATTENUATOR(0x036),
    VFO_AMPLIFIER(0x037),
    VNA_MODE(0x038),

    ANALYSER_REQUEST(0x040),
    ANALYSER_RESPONSE(0x041);

    private int code;

    FrameCommand(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FrameCommand fromCode(int code) {
        for(FrameCommand frameCommand : values()) {
            if(frameCommand.code == code) {
                return frameCommand;
            }
        }
        throw new IllegalArgumentException(String.format("unknown frame command code 0x%04X",code));
    }
}
