package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum VfoType {
    DDS_AD9850("DDS AD9850"), DDS_AD9851("DDS AD9851");

    private String name;

    VfoType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
