package com.mindpart.radio3.config;

import com.mindpart.config.AbstractConfigService;
import com.mindpart.radio3.SweepProfiles;

import java.io.IOException;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.26
 */
public class SweepProfilesService extends AbstractConfigService<SweepProfiles> {
    public SweepProfilesService(String appDirectory) throws IOException {
        super(SweepProfiles.class, appDirectory, "sweep.conf", SweepProfiles.defaults());
        init();
    }
}
