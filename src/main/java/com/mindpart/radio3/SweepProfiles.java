package com.mindpart.radio3;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mindpart.radio3.SweepProfile.withMargin;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.26
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class SweepProfiles {
    public List<SweepProfile> profiles;

    public static final SweepProfiles defaults() {
        List<SweepProfile> profiles = new ArrayList<>();
        profiles.add(withMargin("KF+6m", 1.800, 52.000));
        profiles.add(withMargin("KF", 1.800, 30.000));
        profiles.add(withMargin("160m", 1.800, 2.000));
        profiles.add(withMargin("80m", 3.500, 3.800));
        profiles.add(withMargin("40m", 7.000, 7.200));
        profiles.add(withMargin("30m", 10.100, 10.150));
        profiles.add(withMargin("20m", 14.000, 14.350));
        profiles.add(withMargin("17m", 18.068, 18.168));
        profiles.add(withMargin("15m", 21.000, 21.450));
        profiles.add(withMargin("12m", 24.000, 24.990));
        profiles.add(withMargin("10m", 28.000, 29.700));
        profiles.add(withMargin("6m", 50.000, 52.000));

        SweepProfiles sweepProfiles = new SweepProfiles();
        sweepProfiles.profiles = profiles;
        return sweepProfiles;
    }
}
