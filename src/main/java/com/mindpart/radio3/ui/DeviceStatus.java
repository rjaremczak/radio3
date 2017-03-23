package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.23
 */
public enum DeviceStatus {
    UNKNOWN, READY, ERROR, PROCESSING;

    public String toString() {
        return name().toLowerCase();
    }
}
