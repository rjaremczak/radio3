package com.mindpart.radio3.ui;

import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.23
 */
public enum DeviceStatus {
    DISCONNECTED(Color.GRAY),
    READY(Color.LIGHTGREEN),
    ERROR(Color.RED),
    PROCESSING(Color.ORANGE),
    CONNECTING(Color.GREENYELLOW),
    DISCONNECTING(Color.GREENYELLOW);

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Color mainIndicatorColor;

    DeviceStatus(Color mainIndicatorColor) {
        this.mainIndicatorColor = mainIndicatorColor;
    }

    public Color getMainIndicatorColor() {
        return mainIndicatorColor;
    }

    public String format(UserInterface ui) {
        String name = ui.text("device.status."+name().toLowerCase());
        String lastResponse = ui.text("device.status.lastActivity");
        return name + (this == DISCONNECTED ? "" : " ("+lastResponse+": " + timeFormatter.format(LocalDateTime.now()) +")");
    }
}
