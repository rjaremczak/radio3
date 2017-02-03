package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.12.03
 */
public enum VfoOut {
    DIRECT(FrameCommand.VFO_OUT_DIRECT),
    VNA(FrameCommand.VFO_OUT_VNA);

    private FrameCommand frameCommand;

    VfoOut(FrameCommand frameCommand) {
        this.frameCommand = frameCommand;
    }

    public FrameCommand getFrameCommand() {
        return frameCommand;
    }

    public String toString() {
        return name();
    }
}
