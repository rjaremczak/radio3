package com.mindpart.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.reflect.TypeToken;
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

    private Path ownDirectory;
    private Path configurationFile;
    private String defaultsFileName;
    private TypeToken<T> typeToken = new TypeToken<T>(getClass()) {};

    protected AbstractConfigService(String configFileName, String defaultsFileName) throws IOException {
        ownDirectory = Paths.get(System.getProperty("user.home"), configFileName);
        initDirectory(ownDirectory);
        configurationFile = Paths.get(ownDirectory.toString(),configFileName);

        this.defaultsFileName = defaultsFileName;
        if(!Files.exists(configurationFile, LinkOption.NOFOLLOW_LINKS)) {
            save(loadDefaults());
        }
    }

    private void initDirectory(Path directory) throws IOException {
        if(!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            logger.info("create directory " + directory);
            Files.createDirectory(directory);
        }
    }

    public T load() throws IOException {
        try {
            return new ObjectMapper().readValue(configurationFile.toFile(), (Class<T>) typeToken.getRawType());
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
        return new ObjectMapper().readValue(is, (Class<T>) typeToken.getRawType());
    }

}
