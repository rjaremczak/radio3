package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum HardwareRevision {
    AUTODETECT(0, "Auto"),
    VERSION_1(1, "Ver. 1"),
    VERSION_2(2, "Ver. 2");

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
