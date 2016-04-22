package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.23
 */
public enum FrameCommand {
    DEVICE_RESET(0x000),
    DEVICE_GET_INFO(0x001),
    DEVICE_GET_STATE(0x002),

    VFO_GET_FREQ(0x008),
    VFO_SET_FREQ(0x009),
    VFO_ERROR(0x00f),

    LOGPROBE_GET(0x010),
    LOGPROBE_ERROR(0x017),

    LINPROBE_GET(0x018),
    LINPROBE_ERROR(0x01f),

    CMPPROBE_GET(0x020),
    CMPPROBE_ERROR(0x027),

    FMETER_GET(0x028),
    FMETER_ERROR(0x02f),

    PROBES_GET(0x030),
    PROBES_START_SAMPLING(0x031),
    PROBES_STOP_SAMPLING(0x032),

    ANALYSER_REQUEST(0x040),
    ANALYSER_STOP(0x041),
    ANALYSER_STATE(0x042),
    ANALYSER_DATA(0x043),

    ERROR_INVALID_FRAME(0x03fe),
    LOG_MESSAGE(0x03ff);

    private int code;

    FrameCommand(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FrameCommand of(int code) {
        for(FrameCommand frameCommand : values()) {
            if(frameCommand.code == code) {
                return frameCommand;
            }
        }
        throw new IllegalArgumentException(String.format("unknown frame command code %04X",code));
    }
}
