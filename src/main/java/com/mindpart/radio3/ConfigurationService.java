package com.mindpart.radio3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.08
 */
public class ConfigurationService {
    private static final Logger logger = Logger.getLogger(ConfigurationService.class);

    private Path ownDirectory;
    private Path configurationFile;
    private Configuration configuration;

    private void initOwnDirectory() throws IOException {
        ownDirectory = Paths.get(System.getenv("HOME") + "/.radio3");
        if(!Files.exists(ownDirectory, LinkOption.NOFOLLOW_LINKS)) {
            logger.info("create application's directory " + ownDirectory);
            Files.createDirectory(ownDirectory);
        }
    }

    public void init() throws IOException {
        initOwnDirectory();
        configurationFile = Paths.get(ownDirectory.toString(),"configuration.json");
        if(Files.exists(configurationFile, LinkOption.NOFOLLOW_LINKS)) {
            load();
        } else {
            loadDefaults();
            save();
        }
    }

    public void load() throws IOException {
        configuration = new ObjectMapper().readValue(configurationFile.toFile(), Configuration.class);
    }

    public void save() throws IOException {
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(configurationFile.toFile(), Configuration.class);
    }

    public void loadDefaults() throws IOException {
        InputStream is = getClass().getResourceAsStream("default-configuration.json");
        configuration = new ObjectMapper().readValue(is, Configuration.class);
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
