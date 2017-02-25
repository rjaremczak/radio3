package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.02.21
 */
public enum VnaMode {
    DIRECTIONAL_COUPLER(0, "Directional Coupler"),
    BRIDGE(1, "Bridge");

    private int code;
    private String name;

    VnaMode(int code, String name) {
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
