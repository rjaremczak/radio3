package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    public class Vfo {
        public String name;
        public long minFrequency;
        public long maxFrequency;
    }

    public class FMeter {
        public String name;
        public long minFrequency;
        public long maxFrequency;
    }

    public class LogProbe {
        public String name;
        public long minValue;
        public long maxValue;
        public long minDBm;
        public long maxDBm;
    }

    public String name;
    public String buildId;
    public Vfo vfo = new Vfo();
    public FMeter fMeter = new FMeter();
    public LogProbe logProbe = new LogProbe();
}
