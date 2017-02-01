package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum FrequencyMeterType {
    GENERIC("Generic"), GENERIC_DIV_2("Generic (div:2)"), GENERIC_DIV_4("Generic (div:4)");

    private String name;

    FrequencyMeterType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
