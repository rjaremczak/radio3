package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum LinearProbeType {
    GENERIC("Generic");

    private String name;

    LinearProbeType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
