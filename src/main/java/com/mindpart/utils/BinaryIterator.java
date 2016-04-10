package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class BinaryIterator {
    private byte[] buffer;
    private int index;

    public BinaryIterator(byte[] buffer) {
        this.buffer = buffer;
        this.index = 0;
    }

    public int nextUInt8() {
        return Binary.toUInt8(buffer, index++);
    }

    public boolean nextBool() {
        return nextUInt8() != 0;
    }

    public int nextUInt16() {
        int val = Binary.toUInt16(buffer, index);
        index += 2;
        return val;
    }

    public long nextUInt32() {
        long val = Binary.toUInt32(buffer, index);
        index += 4;
        return val;
    }

    public String nextString(int length) {
        String str = new String(buffer, index, length);
        index += length;
        return str;
    }
}