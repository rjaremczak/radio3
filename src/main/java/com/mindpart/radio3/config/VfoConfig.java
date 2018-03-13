package com.mindpart.radio3.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by Robert Jaremczak
 * Date: 2018.03.10
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class VfoConfig {
    public enum Type {
        DDS_AD9850, DDS_AD9851;

        @Override
        public String toString() {
            return name().replace('_', ' ');
        }
    }

    Type type;
    int offset;

    public Type getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public static VfoConfig defaults() {
        VfoConfig config = new VfoConfig();
        config.type = Type.DDS_AD9851;
        config.offset = 0;
        return config;
    }
}
