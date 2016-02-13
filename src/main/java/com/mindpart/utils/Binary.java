package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Binary {
    private Binary() {
    }

    public static int uint8low(int val) {
        return val & 0xff;
    }

    public static int uint8high(int val) {
        return (val >> 8) & 0xff;
    }

    public static int uint16(byte[] bytes) {
        return (bytes[0] & 0xff) + ((bytes[1] << 8) & 0xff00);
    }

    public static int uint16(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff) + ((bytes[offset+1] << 8) & 0xff00);
    }

    public static int uint8(byte[] bytes, int offset) {
        return bytes[offset] & 0xff;
    }
}
