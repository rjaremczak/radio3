package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.16
 */
public enum LogLevel {
    DEBUG(0, "debug"),
    INFO(1, "info"),
    ERROR(2, "error");

    private int code;
    private String name;

    LogLevel(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
