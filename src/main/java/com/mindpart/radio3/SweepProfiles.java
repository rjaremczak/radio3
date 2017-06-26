package com.mindpart.radio3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.26
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class SweepProfiles {
    public List<SweepProfile> profiles;
}
