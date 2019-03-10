package org.featuretoggle.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestUtils {

    public static Response requestToggleValue(final String toggleId, final String toggleServerUri) {
        log.info("Checking for toggle value for toggleId={} and toggleServerUri={}", toggleId, toggleServerUri);
        return ClientBuilder.newClient().target(toggleServerUri)
            .queryParam("toggleId", toggleId)
            .path("toggle-server/get-toggle")
            .request()
            .get();
    }
}
