package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum AttenuatorMode {
    NONE("No Attenuator");

    private String name;

    AttenuatorMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
