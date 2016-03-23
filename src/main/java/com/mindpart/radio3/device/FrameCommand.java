package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.23
 */
public enum FrameCommand {
    DEVICE_RESET(0x000),
    DEVICE_GET_INFO(0x001),

    VFO_GET_FREQ(0x008),
    VFO_SET_FREQ(0x009),
    VFO_ERROR(0x00f),

    LOGPROBE_SAMPLE(0x010),
    LOGPROBE_SAMPLING_ON(0x011),
    LOGPROBE_SAMPLING_OFF(0x012),
    LOGPROBE_ERROR(0x017),

    LINPROBE_SAMPLE(0x018),
    LINPROBE_SAMPLING_ON(0x019),
    LINPROBE_SAMPLING_OFF(0x01a),
    LINPROBE_ERROR(0x01f),

    CMPPROBE_SAMPLE(0x020),
    CMPPROBE_SAMPLING_ON(0x021),
    CMPPROBE_SAMPLING_OFF(0x022),
    CMPPROBE_ERROR(0x027),

    FMETER_SAMPLE(0x028),
    FMETER_SAMPLING_ON(0x029),
    FMETER_SAMPLING_OFF(0x02a),
    FMETER_ERROR(0x02f),

    STATUS_INVALID_FRAME(0x03fe),
    STATUS_OK(0x03ff);

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
