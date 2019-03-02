package client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class ToggleClient {
    public static String isFeatureEnabled(final String toggleId) {
        Client client = ClientBuilder.newClient();

        Response response = client.target("http://localhost:8090")
            .queryParam("toggleId", toggleId)
            .path("toggle-server/get-toggle")
            .request()
            .get();

        String readEntity = response.readEntity(String.class);
        return readEntity;
    }
}
