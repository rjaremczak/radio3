package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public final class Crc8 {
    private short crc;

    public Crc8() {
        crc = 0;
    }

    public void add(byte[] buf) {
        for(byte data : buf) { add(data); }
    }

    public void add(byte data) {
        for(int i=8; i>0; i--) {
            int sum = (crc ^ data) & 1;
            crc >>= 1;
            if(sum != 0) {
                crc ^= 0x8C;
            }
            data >>= 1;
        }
    }

    public int getCrc() {
        return crc;
    }

    public static class Error extends Exception {
        public Error(int expected, int actual) {
            super(String.format("CRC8 mismatch, expected %02X but was %02X",expected,actual));
        }
    }

}
