package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.02
 */
public enum ConnectionStatus {
    DISCONNECTED("disconnected"),
    CONNECTING("connecting..."),
    DEVICE_ERROR("device error"),
    CONNECTION_TIMEOUT("connection timeout"),
    CONNECTED("connected");

    private String text;

    ConnectionStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
