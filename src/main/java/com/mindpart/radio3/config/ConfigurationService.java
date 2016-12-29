package com.mindpart.radio3.config;

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

    private void initOwnDirectories() throws IOException {
        ownDirectory = Paths.get(System.getProperty("user.home"),".radio3");
        initDirectory(ownDirectory);
        //initDirectory(Paths.get(ownDirectory.toString(),"log"));
    }

    private void initDirectory(Path directory) throws IOException {
        if(!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            logger.info("create directory " + directory);
            Files.createDirectory(directory);
        }
    }

    public void init() throws IOException {
        initOwnDirectories();
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
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(configurationFile.toFile(), configuration);
    }

    public void loadDefaults() throws IOException {
        InputStream is = getClass().getResourceAsStream("default-configuration.json");
        configuration = new ObjectMapper().readValue(is, Configuration.class);
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
