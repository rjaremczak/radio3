package com.mindpart.radio3.config;

import com.mindpart.config.AbstractConfigService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */
public class ConfigurationService extends AbstractConfigService<Configuration> {
    private String buildId;

    public ConfigurationService() throws IOException {
        super("configuration.json","default-configuration.json");
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("common.properties"));
        buildId = properties.getProperty("buildId");
    }

    public String getBuildId() {
        return buildId;
    }
}
