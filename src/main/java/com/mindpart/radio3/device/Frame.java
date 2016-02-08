package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Frame {
    public static final int HEADER_SIZE = 2;
    public static final int MAX_PAYLOAD_SIZE = 65535;

    protected Type type;
    protected int payloadSize;
    protected byte[] payload;

    public static class Type {
        private int code;

        public Type(int code) {
            this.code = code;
        }

        public boolean hasPayload() {
            return (code & 0x8000) != 0;
        }

        public int getCode() {
            return code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Type)) return false;

            Type type = (Type) o;

            return code == type.code;

        }

        @Override
        public int hashCode() {
            return code;
        }
    }

    protected Frame(Type type) {
        this.type = type;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    public Type getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }

    protected void setPayload(byte[] payload) {
        if(payload.length > MAX_PAYLOAD_SIZE) {
            throw new IllegalArgumentException("payload can't exceed "+MAX_PAYLOAD_SIZE+" bytes");
        }
        this.payload = payload;
        this.payloadSize = payload!=null ? payload.length + HEADER_SIZE : 0;
    }
}
