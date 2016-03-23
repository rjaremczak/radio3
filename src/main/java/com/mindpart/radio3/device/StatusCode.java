package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.23
 */
public class StatusCode {
    private FrameCommand frameCommand;
    private int auxCode;

    public StatusCode(FrameCommand frameCommand, int auxCode) {
        this.frameCommand = frameCommand;
        this.auxCode = auxCode;
    }

    public StatusCode(FrameCommand frameCommand) {
        this(frameCommand, -1);
    }

    public FrameCommand getFrameCommand() {
        return frameCommand;
    }

    public int getAuxCode() {
        return auxCode;
    }

    public boolean hasAuxCode() {
        return auxCode >= 0;
    }

    @Override
    public String toString() {
        return frameCommand+(hasAuxCode() ? String.format(":%02X", auxCode) : "");
    }
}
