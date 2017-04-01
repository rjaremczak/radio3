package com.mindpart.radio3.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.23
 */
public enum DeviceStatus {
    UNKNOWN("no device"), READY("ready"), ERROR("error"), PROCESSING("processing");

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String name;

    DeviceStatus(String name) {
        this.name = name;
    }

    public String toString() {
        return name + (this == UNKNOWN ? "" : " (last response: " + timeFormatter.format(LocalDateTime.now()) +")");
    }
}
