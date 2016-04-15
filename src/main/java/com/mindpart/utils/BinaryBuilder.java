package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class BinaryBuilder {
    private byte[] bytes;
    private int index = 0;

    public BinaryBuilder(int size) {
        bytes = new byte[size];
    }

    public void addUInt8(int value) {
        Binary.storeUInt8(bytes, index++, value);
    }

    public void addUInt16(int value) {
        Binary.storeUInt16(bytes, index, value);
        index += 2;
    }

    public void addUInt32(long value) {
        Binary.storeUInt32(bytes, index, value);
        index += 4;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
