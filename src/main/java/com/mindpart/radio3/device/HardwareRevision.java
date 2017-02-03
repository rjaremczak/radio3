package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum HardwareRevision {
    UNKNOWN(0, "Unknown"),
    PROTOTYPE_1(1, "Prototype 1"),
    PROTOTYPE_2(2, "Prototype 2");

    private int code;
    private String name;

    HardwareRevision(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
