package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.14
 */
public enum FilterInfoType {
    BANDPASS("info.bandfilter.bandpass"),
    BANDSTOP("info.bandfilter.bandstop");

    private String name;

    FilterInfoType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
