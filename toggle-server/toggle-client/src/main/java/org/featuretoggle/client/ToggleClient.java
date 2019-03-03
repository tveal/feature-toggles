package org.featuretoggle.client;

import static org.featuretoggle.client.ConnectorUtils.getResourceAsStream;
import static org.featuretoggle.client.ConnectorUtils.requestToggleValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToggleClient {
    public static final String APP_PROPS = "application.properties";

    public static boolean isFeatureEnabled(final String toggleId) {
        boolean returnValue;
        Properties props = getAppProperties();

        try {
            String toggleServerUri = props.getProperty("toggleServerUri");

            if (toggleServerUri != null) {
                Response response = requestToggleValue(toggleId, toggleServerUri);
                String respValue = response.readEntity(String.class);
                log.info("server response for toggleId: {}, value: {}", toggleId, respValue);
                returnValue = Boolean.parseBoolean(respValue);
            } else {
                log.error("toggleServerUri not found in {}", APP_PROPS);
                returnValue = getToggleValueFromProps(toggleId, props);
            }
        } catch (ProcessingException e) {
            log.error("toggle-server is unavailable, checking {} for {};", APP_PROPS, toggleId, e);
            returnValue = getToggleValueFromProps(toggleId, props);
        }

        return returnValue;
    }

    private static boolean getToggleValueFromProps(final String toggleId, final Properties props) {
        String togglePropValue = props.getProperty(toggleId);
        boolean boolValue = false;
        if (togglePropValue != null) {
            log.info("Found {} value: {}, from {}", toggleId, togglePropValue, APP_PROPS);
            boolValue = Boolean.parseBoolean(togglePropValue);
        } else {
            log.error("{} not found in {}, defaulting to false", toggleId, APP_PROPS);
        }
        return boolValue;
    }

    private static Properties getAppProperties() {
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
