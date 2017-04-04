package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.02
 */
public enum ConnectionStatus {
    DISCONNECTED("disconnected"),
    CONNECTING("connecting..."),
    CONNECTION_ERROR("connection error"),
    CONNECTED("connected");

    private String text;

    ConnectionStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
