package org.featuretoggle.client;

import static org.featuretoggle.client.RestUtils.requestToggleValue;
import static org.featuretoggle.client.FileUtils.APP_PROPS;
import static org.featuretoggle.client.FileUtils.getAppProperties;

import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestToggleRetriever extends ToggleRetriever {

    @Override
    protected Boolean getToggleFromServer(final String toggleId) {
        Boolean toggleValue = null;
        String toggleServerUri = getAppProperties().getProperty("toggleServerUri");

        if (toggleServerUri != null) {

            Response response = requestToggleValue(toggleId, toggleServerUri);
            String respValue = response.readEntity(String.class);
            toggleValue = Boolean.valueOf(respValue);

            log.info("server response for toggleId: {}, value: {}", toggleId, respValue);

        } else {
            log.error("toggleServerUri not found in {}", APP_PROPS);
        }
        return toggleValue;
    }

}
