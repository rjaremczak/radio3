package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.12.03
 */
public enum VfoOut {
    VFO(FrameCmd.SET_VFO_TO_SOCKET),
    VNA(FrameCmd.SET_VFO_TO_VNA);

    private FrameCmd frameCmd;

    VfoOut(FrameCmd frameCmd) {
        this.frameCmd = frameCmd;
    }

    public FrameCmd getFrameCmd() {
        return frameCmd;
    }

    public String toString() {
        return name();
    }
}
