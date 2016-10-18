package com.mindpart.radio3;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.11
 */
public class ConfigurationServiceTest {
    ConfigurationService configurationService = new ConfigurationService();

    @Test
    public void testLoadDefaults() throws IOException {
        configurationService.loadDefaults();
    }
}