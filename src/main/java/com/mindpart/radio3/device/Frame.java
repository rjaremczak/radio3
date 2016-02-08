package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Frame {
    public static final int HEADER_SIZE = 4;
    public static final int MAX_PAYLOAD_SIZE = 65535 - HEADER_SIZE;

    private int size;
    private int type;
    private byte[] payload;

    public Frame(int type, byte[] payload) {
        if(payload.length > MAX_PAYLOAD_SIZE) {
            throw new IllegalArgumentException("payload can't exceed "+MAX_PAYLOAD_SIZE+" bytes");
        }

        this.type = type;
        this.payload = payload;
        this.size = payload.length + HEADER_SIZE;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }
}
