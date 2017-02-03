package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum VfoType {
    NONE(0, "Not installed"),
    DDS_AD9850(1, "DDS AD9850"),
    DDS_AD9851(2, "DDS AD9851");

    private int code;
    private String name;

    VfoType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }
}
