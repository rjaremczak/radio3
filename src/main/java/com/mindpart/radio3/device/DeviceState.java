package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceState {
    boolean probesSampling;
    int samplingPeriodMs;
    long timeMs;

    public DeviceState(boolean probesSampling, int samplingPeriodMs, long timeMs) {
        this.probesSampling = probesSampling;
        this.samplingPeriodMs = samplingPeriodMs;
        this.timeMs = timeMs;
    }

    public boolean isProbesSampling() {
        return probesSampling;
    }

    public int getSamplingPeriodMs() {
        return samplingPeriodMs;
    }

    public long getTimeMs() {
        return timeMs;
    }
}
