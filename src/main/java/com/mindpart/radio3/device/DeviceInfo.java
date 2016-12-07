package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public class DeviceInfo {
    public static class Vfo {
        public enum Type {
            NONE(0, "not installed"),
            AD9850(1, "AD9850 (layout 1)"),
            AD9851(2, "AD9851 (layout 1)");

            private int code;
            private String description;

            Type(int code, String description) {
                this.code = code;
                this.description = description;
            }

            public int getCode() {
                return code;
            }

            public String getDescription() {
                return description;
            }
        }

        public Type type;
        public long minFrequency;
        public long maxFrequency;
    }

    public String name;
    public String buildId;
    public Vfo vfo = new Vfo();
}
