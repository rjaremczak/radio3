package com.mindpart.radio3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class SweepProfile {
    private static final double MARGIN_MHZ = 0.1;

    public String name;
    public double freqMin;
    public double freqMax;

    @Override
    public String toString() {
        return name;
    }

    public static final SweepProfile withMargin(String name, double fromMhz, double toMhz) {
        SweepProfile sp = new SweepProfile();
        sp.name = name;
        sp.freqMin = fromMhz - MARGIN_MHZ;
        sp.freqMax = toMhz + MARGIN_MHZ;
        return sp;
    }
}
