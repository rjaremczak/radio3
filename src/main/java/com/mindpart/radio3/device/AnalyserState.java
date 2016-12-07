package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public enum AnalyserState {
    READY("ready"),
    PROCESSING("processing..."),
    INVALID_REQUEST("invalid request");

    private String text;

    AnalyserState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
