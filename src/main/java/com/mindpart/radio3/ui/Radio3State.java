package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.02
 */
public enum Radio3State {
    DISCONNECTED("disconnected"),
    CONNECTING("connecting..."),
    DEVICE_ERROR("device error"),
    CONNECTION_TIMEOUT("connection timeout"),
    CONNECTED("connected");

    private String text;

    Radio3State(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
