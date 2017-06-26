package com.mindpart.radio3.config;

import com.mindpart.config.AbstractConfigService;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */
public class ConfigurationService extends AbstractConfigService<Configuration> {
    private final String buildId;

    public ConfigurationService(String appDirectory) throws IOException {
        super(Configuration.class, appDirectory, "local.conf","default-local.conf");
        Properties properties = new Properties();
        properties.load(new InputStreamReader(getClass().getResourceAsStream("common.properties"), "UTF-8"));
        buildId = properties.getProperty("buildId");
        init();
    }

    public String getBuildId() {
        return buildId;
    }

    @Override
    public void save(Configuration data) {
        data.setCreatedBy(buildId);
        super.save(data);
    }
}
