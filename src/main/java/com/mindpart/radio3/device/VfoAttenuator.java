package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2017.01.30
 */
public enum VfoAttenuator {
    OFF(0, "Off"),
    LEVEL_1(1, "Level 1"),
    LEVEL_2(2, "Level 2"),
    LEVEL_3(3, "Level 3"),
    LEVEL_4(4, "Level 4"),
    LEVEL_5(5, "Level 5"),
    LEVEL_6(6, "Level 6"),
    LEVEL_7(7, "Level 7");

    private int code;
    private String name;

    VfoAttenuator(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
