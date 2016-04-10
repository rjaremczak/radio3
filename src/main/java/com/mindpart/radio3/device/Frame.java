package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;
import com.mindpart.utils.BinaryIterator;

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

    @Override
    public String toString() {
        return command + ", payload size: "+getPayloadSize();
    }

    public BinaryIterator binaryIterator() {
        return new BinaryIterator(payload);
    }
}
