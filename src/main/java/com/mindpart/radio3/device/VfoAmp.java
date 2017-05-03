package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.02.21
 */
public enum VfoAmp {
    OFF(0, "Off"), ON(1, "On");

    private int code;
    private String name;

    VfoAmp(int code, String name) {
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
