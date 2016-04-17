package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Binary {
    private Binary() {
    }

    public static int toUInt8low(int val) {
        return val & 0xff;
    }

    public static int toUInt8high(int val) {
        return (val >> 8) & 0xff;
    }

    public static long toUInt16low(long val) {
        return val & 0xffff;
    }

    public static long toUInt16high(long val) {
        return (val >> 16) & 0xffff;
    }

    public static int toUInt8(byte[] bytes) {
        return toUInt8(bytes, 0);
    }

    public static int toUInt8(byte[] bytes, int offset) {
        return bytes[offset] & 0xff;
    }

    public static int toUInt16(byte[] bytes) {
        return (bytes[0] & 0xff) + ((bytes[1] << 8) & 0xff00);
    }

    public static int toUInt16(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff) + ((bytes[offset+1] << 8) & 0xff00);
    }

    public static long toUInt32(byte[] bytes) {
        return (bytes[0] & 0xffL) +
                ((bytes[1] << 8) & 0x0000ff00L) +
                ((bytes[2] << 16) & 0x00ff0000L) +
                ((bytes[3] << 24) & 0xff000000L);
    }

    public static long toUInt32(byte[] bytes, int offset) {
        return (bytes[offset] & 0xffL) +
                ((bytes[offset+1] << 8) & 0x0000ff00L) +
                ((bytes[offset+2] << 16) & 0x00ff0000L) +
                ((bytes[offset+3] << 24) & 0xff000000L);
    }

    public static byte[] fromUInt32(int val) {
        return new byte[]{
                (byte) (val & 0xff),
                (byte) ((val >> 8) & 0xff),
                (byte) ((val >> 16) & 0xff),
                (byte) ((val >> 24) & 0xff)
        };
    }

    public static void storeUInt32(byte[] bytes, int offset, long val) {
        bytes[offset++] = (byte) (val & 0xff);
        bytes[offset++] = (byte) ((val >> 8) & 0xff);
        bytes[offset++] = (byte) ((val >> 16) & 0xff);
        bytes[offset] = (byte) ((val >> 24) & 0xff);
    }

    public static void storeUInt16(byte[] bytes, int offset, int val) {
        bytes[offset++] = (byte) (val & 0xff);
        bytes[offset] = (byte) ((val >> 8) & 0xff);
    }

    public static void storeUInt8(byte[] bytes, int offset, int val) {
        bytes[offset] = (byte) (val & 0xff);
    }
}
