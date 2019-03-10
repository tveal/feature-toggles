package org.featuretoggle.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    public static final String APP_PROPS = "application.properties";

    private static InputStream getResourceAsStream(final String filename) {
        return FileUtils.class.getClassLoader().getResourceAsStream(filename);
    }

    public static Properties getAppProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = getResourceAsStream(APP_PROPS);
            if (input == null) {
                log.warn("Sorry, unable to find classpath resource {}", APP_PROPS);
                return prop;
            }
            prop.load(input);
        } catch (IOException e) {
            log.error("Problem getting properties file: {}; {}", APP_PROPS, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("Failed closing input file: {}; {}", APP_PROPS, e);
                }
            }
        }
        return prop;
    }
}
