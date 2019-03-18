package org.featuretoggle.client;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientBuilder.class })
public class RestUtilsTest {

    @Mock
    Client client;

    @Mock
    WebTarget webTarget;

    @Mock
    Builder builder;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(ClientBuilder.class);
        mockClientBuilder();

        when(client.target(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.queryParam(Mockito.anyString(), Mockito.any())).thenReturn(webTarget);
        when(webTarget.path(Mockito.anyString())).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(builder);
    }

    @Test
    public void requestToggleValue_shouldCallClientBuilder_withProperParams() {
        String toggleId = "blue-feature";
        String toggleServerUri = "dev.server:1234";
        RestUtils.requestToggleValue(toggleId, toggleServerUri);

        verify(client).target(toggleServerUri);
        verify(webTarget).queryParam("toggleId", toggleId);
        verify(webTarget).path("toggle-server/get-toggle");
    }

    private void mockClientBuilder() throws Exception {
        BDDMockito.given(ClientBuilder.newClient()).willReturn(client);
    }
}
