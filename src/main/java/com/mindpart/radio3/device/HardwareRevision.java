package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum HardwareRevision {
    PROTOTYPE_1("Prototype 1"), PROTOTYPE_2("Prototype 2");

    private String name;

    HardwareRevision(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
