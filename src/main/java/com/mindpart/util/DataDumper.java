package com.mindpart.util;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.22
 */
public interface DataDumper {
    static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(bytes.length).append(") ");
        for (byte aByte : bytes) {
            sb.append(String.format("%02X ", aByte));
        }
        return sb.toString().trim();
    }
}
