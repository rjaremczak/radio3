package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.23
 */
public enum DeviceStatus {
    UNKNOWN(""), READY("ready"), ERROR("error"), PROCESSING("processing");

    private String name;

    DeviceStatus(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
