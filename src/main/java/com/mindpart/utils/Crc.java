package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class Crc {

    private Crc() {
    }

    public static int crc8(byte[] data, int start, int length) {
        short crc = 0;
        for(int pos = start; pos<start+length; pos++) {
            short b = data[pos];
            for(int i=8; i>0; i--) {
                int sum = (crc ^ b) & 1;
                crc >>= 1;
                if(sum != 0) {
                    crc ^= 0x8C;
                }
                b >>= 1;
            }
        }

        return (byte)crc;
    }

    public static int crc16(byte[] data, int start, int length) {
        int crc = 0;
        for (int pos = start; pos < length; pos++) {
            for (int i = 0x80; i != 0; i >>= 1) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x8005;
                } else {
                    crc = crc << 1;
                }
                if((data[pos] & i) != 0) {
                    crc ^= 0x8005;
                }
            }
        }
        return (short)crc;
    }
}
