package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.16
 */
public class Frame {
    private int type;
    private byte[] payload;

    public Frame(int type) {
        this(type, null);
    }

    public Frame(int type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getPayloadSize() {
        return payload != null ? payload.length : 0;
    }

    public int getByte(int offset) {
        return Binary.uInt8(payload, offset);
    }

    public int getWord(int offset) {
        return Binary.uInt16(payload, offset);
    }

    public int getDWord(int offset) {
        return Binary.uInt32(payload, offset);
    }

    @Override
    public String toString() {
        return String.format("%03X, payload size: %d", type, getPayloadSize());
    }

}
