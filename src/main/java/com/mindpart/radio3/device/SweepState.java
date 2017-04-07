package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public enum SweepState {
    READY("ready"),
    PROCESSING("processing..."),
    INVALID_REQUEST("invalid request");

    private String text;

    SweepState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
