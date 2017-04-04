package com.mindpart.radio3.ui;

import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.23
 */
public enum DeviceStatus {
    DISCONNECTED("disconnected", Color.GRAY),
    READY("ready", Color.LIGHTGREEN),
    ERROR("error", Color.RED),
    PROCESSING("processing", Color.ORANGE),
    CONNECTING("connecting", Color.GREENYELLOW),
    DISCONNECTING("disconnecting", Color.GREENYELLOW);

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String name;
    private Color mainIndicatorColor;

    DeviceStatus(String name, Color mainIndicatorColor) {
        this.name = name;
        this.mainIndicatorColor = mainIndicatorColor;
    }

    public Color getMainIndicatorColor() {
        return mainIndicatorColor;
    }

    public String toString() {
        return name + (this == DISCONNECTED ? "" : " (last response: " + timeFormatter.format(LocalDateTime.now()) +")");
    }
}
