package com.mindpart.radio3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class SweepProfile {
    public String name;
    public String freqMin;
    public String freqMax;

    public String dump() {
        return name+" - freq: "+freqMin+" - "+freqMax;
    }

    @Override
    public String toString() {
        return name;
    }
}
