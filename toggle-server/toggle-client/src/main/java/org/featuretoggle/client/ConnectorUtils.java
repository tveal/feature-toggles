package org.featuretoggle.client;

import java.io.InputStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class ConnectorUtils {

    public static Response requestToggleValue(final String toggleId, final String toggleServerUri) {
        return ClientBuilder.newClient().target(toggleServerUri)
            .queryParam("toggleId", toggleId)
            .path("toggle-server/get-toggle")
            .request()
            .get();
    }

    public static InputStream getResourceAsStream(final String filename) {
        return ToggleClient.class.getClassLoader().getResourceAsStream(filename);
    }
}
