package com.mindpart.radio3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */
public class ConfigurationService {
    private static final Logger logger = Logger.getLogger(ConfigurationService.class);

    private Path ownDirectory;
    private Path configurationFile;
    private String buildId;

    private void initOwnDirectories() throws IOException {
        ownDirectory = Paths.get(System.getProperty("user.home"),".radio3");
        initDirectory(ownDirectory);
    }

    private void initDirectory(Path directory) throws IOException {
        if(!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            logger.info("create directory " + directory);
            Files.createDirectory(directory);
        }
    }

    public void init() throws IOException {
        loadCommonProperties();
        initOwnDirectories();
        configurationFile = Paths.get(ownDirectory.toString(),"configuration.json");
        if(!Files.exists(configurationFile, LinkOption.NOFOLLOW_LINKS)) {
            save(loadDefaults());
        }
    }

    private void loadCommonProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("common.properties"));
        buildId = properties.getProperty("buildId");
    }

    public Configuration load() throws IOException {
        try {
            return new ObjectMapper().readValue(configurationFile.toFile(), Configuration.class);
        } catch (InvalidFormatException e) {
            logger.error("error parsing configuration file, delete and replace with defaults");
            Files.delete(configurationFile);
            Configuration defaults = loadDefaults();
            save(defaults);
            return defaults;
        }
    }

    public void save(Configuration configuration) {
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(configurationFile.toFile(), configuration);
        } catch (IOException e) {
            logger.error(e,e);
        }
    }

    public Configuration loadDefaults() throws IOException {
        InputStream is = getClass().getResourceAsStream("default-configuration.json");
        return new ObjectMapper().readValue(is, Configuration.class);
    }

    public String getBuildId() {
        return buildId;
    }
}
