package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.16
 */
public class Frame {
    private FrameCommand command;
    private byte[] payload;

    public Frame(FrameCommand command) {
        this(command, null);
    }

    public Frame(FrameCommand command, byte[] payload) {
        this.command = command;
        this.payload = payload;
    }

    public FrameCommand getCommand() {
        return command;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getPayloadSize() {
        return payload != null ? payload.length : 0;
    }

    public int getUInt8(int offset) {
        return Binary.toUInt8(payload, offset);
    }

    public int getUInt16(int offset) {
        return Binary.toUInt16(payload, offset);
    }

    public long getUInt32(int offset) {
        return Binary.toUInt32(payload, offset);
    }

    @Override
    public String toString() {
        return command + ", payload size: "+getPayloadSize();
    }

}
