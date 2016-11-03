package com.mindpart.radio3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class SweepProfile {
    public static final int DEFAULT_STEPS = 500;

    public String name;
    public String freqMin;
    public String freqMax;
    public int steps = DEFAULT_STEPS;

    @Override
    public String toString() {
        return name;
    }
}
