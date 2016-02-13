package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */

class Frame {
    private int header;
    private int payloadSize;
    private byte[] payload;

    Frame(int header) {
        this.header = header;
    }

    boolean hasPayload() {
        return (header & 0x8000) != 0;
    }

    boolean hasCrc16() {
        return (header & 0x4000) != 0;
    }

    void setPayload(byte[] bytes) {
        if(!hasPayload()) {
            throw new UnsupportedOperationException("no payload allowed for this frame");
        }

        if(bytes.length > 65535) {
            throw new IllegalArgumentException("payload too large");
        }

        payloadSize = bytes.length;
        payload = bytes;
    }

    public int getHeader() {
        return header;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getByte(int offset) {
        return Binary.uint8(payload, offset);
    }

    public int getWord(int offset) {
        return Binary.uint16(payload, offset);
    }

    @Override
    public String toString() {
        return String.format("%04X, payload size: %d", header, payloadSize);
    }

}
