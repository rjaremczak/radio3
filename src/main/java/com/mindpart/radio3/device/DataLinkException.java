package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.27
 */
public class DataLinkException extends Exception {
    private static final String DEFAULT_MESSAGE = "device connection error";
    private boolean connected;

    public DataLinkException(boolean connected, Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }
}
