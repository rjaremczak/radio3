package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum VnaModule {
    GAIN_PHASE("Gain & Phase");

    private String name;

    VnaModule(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
