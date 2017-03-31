package com.mindpart.utils;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.22
 */
public interface DataDumper {
    static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("("+bytes.length+") ");
        for(int i=0; i<bytes.length; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString().trim();
    }
}
