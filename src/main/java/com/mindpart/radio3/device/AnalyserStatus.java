package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class AnalyserStatus {
    public enum State { IDLE, IN_PROGRESS, INVALID_REQUEST }

    private State state;

    public AnalyserStatus(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
