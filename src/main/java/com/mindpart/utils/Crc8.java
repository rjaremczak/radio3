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

    public void process(byte[] bytes, final int readPos, final int length) {
        final int indexMax = readPos + length;
        for(int i=readPos; i<indexMax; i++) {
            process(bytes[i]);
        }
    }

    public void process(byte[] buf) {
        for(byte data : buf) { process(data); }
    }

    public void process(int word) {
        process((byte) Binary.toUInt8low(word));
        process((byte) Binary.toUInt8high(word));
    }

    public void process(byte data) {
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
        public Error(int calculated, int received) {
            super(String.format("CRC error: calculated %02X, received %02X", calculated, received));
        }
    }

}
