package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Binary {
    private Binary() {
    }

    public static int lowByte(int val) {
        return val & 0xff;
    }

    public static int highByte(int val) {
        return (val >> 8) & 0xff;
    }

    public static int word(byte[] bytes) {
        return (bytes[0] & 0xff) + ((bytes[1] << 8) & 0xff00);
    }
}
