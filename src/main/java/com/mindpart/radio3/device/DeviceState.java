package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.10
 */
public class DeviceState {
    boolean probesSampling;
    int samplingPeriodMs;
    long timeMs;
    AnalyserState analyserState;
    DdsOut ddsOut;

    public DeviceState(boolean probesSampling, int samplingPeriodMs, long timeMs, AnalyserState analyserState, DdsOut ddsOut) {
        this.probesSampling = probesSampling;
        this.samplingPeriodMs = samplingPeriodMs;
        this.timeMs = timeMs;
        this.analyserState = analyserState;
        this.ddsOut = ddsOut;
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

    public AnalyserState getAnalyserState() {
        return analyserState;
    }

    public DdsOut getDdsOut() {
        return ddsOut;
    }
}
