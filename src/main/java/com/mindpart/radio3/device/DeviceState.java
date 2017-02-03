package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceState {
    public boolean probesSampling;
    public int samplingPeriodMs;
    public long timeMs;
    public AnalyserState analyserState;
    public VfoOut vfoOut;
    public VfoAttenuator vfoAttenuator;
}
