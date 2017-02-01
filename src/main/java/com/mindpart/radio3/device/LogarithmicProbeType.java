package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum LogarithmicProbeType {
    GENERIC("Generic");

    private String name;

    LogarithmicProbeType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
