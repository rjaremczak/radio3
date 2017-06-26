package com.mindpart.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.20
 */
public abstract class AbstractConfigService<T> {
    private static final Logger logger = Logger.getLogger(AbstractConfigService.class);

    private final Path ownDirectory;
    private final Path configurationFile;
    private final String defaultsFileName;
    private final Class<T> theClass;

    protected AbstractConfigService(Class<T> theClass, String appDirectory, String configFileName, String defaultsFileName) {
        this.theClass = theClass;
        this.defaultsFileName = defaultsFileName;
        this.ownDirectory = Paths.get(System.getProperty("user.home"), appDirectory);
        this.configurationFile = Paths.get(ownDirectory.toString(),configFileName);
    }

    protected void init() throws IOException {
        if(!Files.exists(ownDirectory, LinkOption.NOFOLLOW_LINKS)) {
            logger.info("create directory " + ownDirectory);
            Files.createDirectory(ownDirectory);
        }
        
        if(!Files.exists(configurationFile, LinkOption.NOFOLLOW_LINKS)) {
            save(loadDefaults());
        }
    }

    public T load() throws IOException {
        try {
            return new ObjectMapper().readValue(configurationFile.toFile(), theClass);
        } catch (InvalidFormatException e) {
            logger.error("error parsing file "+configurationFile.getFileName()+", delete and replace with defaults");
            Files.delete(configurationFile);
            T defaults = loadDefaults();
            save(defaults);
            return defaults;
        }
    }

    public void save(T data) {
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(configurationFile.toFile(), data);
        } catch (IOException e) {
            logger.error(e,e);
        }
    }

    public T loadDefaults() throws IOException {
        InputStream is = getClass().getResourceAsStream(defaultsFileName);
        return new ObjectMapper().readValue(is, theClass);
    }

}
