package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Binary {
    private Binary() {
    }

    public static int uInt8low(int val) {
        return val & 0xff;
    }

    public static int uInt8high(int val) {
        return (val >> 8) & 0xff;
    }

    public static int uInt8(byte[] bytes, int offset) {
        return bytes[offset] & 0xff;
    }

    public static int uInt16(byte[] bytes) {
        return (bytes[0] & 0xff) + ((bytes[1] << 8) & 0xff00);
    }

    public static int uInt16(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff) + ((bytes[offset+1] << 8) & 0xff00);
    }

    public static int uInt32(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff) +
                ((bytes[offset+1] << 8) & 0x0000ff00) +
                ((bytes[offset+2] << 16) & 0x00ff0000) +
                ((bytes[offset+3] << 24) & 0xff000000);
    }
}
