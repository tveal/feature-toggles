package org.featuretoggle.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

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
@PrepareForTest({ ConnectorUtils.class })
public class ToggleClientTest {

    @Mock
    Response response;

    @Before
    public void setup() {
        PowerMockito.mockStatic(ConnectorUtils.class);
    }

    @Test
    public void isFeatureEnabled_shouldLoadToggleValuesFromProps_orDefaultFalse() throws Exception {
        List<String> mockAppProps = new ArrayList<>();
        mockAppProps.add("toggle1=false");
        mockAppProps.add("toggle2=true");

        mockAppProps(mockAppProps);
        assertThat(ToggleClient.isFeatureEnabled("toggle1")).isFalse();
        mockAppProps(mockAppProps);
        assertThat(ToggleClient.isFeatureEnabled("toggle2")).isTrue();
        mockAppProps(mockAppProps);
        assertThat(ToggleClient.isFeatureEnabled("toggle3")).isFalse();
    }

    @Test
    public void isFeatureEnabled_shouldReturnValueFromServer() throws Exception {
        String toggleServerUri = "fakehost:8090";
        String toggleId = "toggle1";

        List<String> mockAppProps = new ArrayList<>();
        mockAppProps.add(toggleId + "=true");
        mockAppProps.add("toggleServerUri=" + toggleServerUri);

        mockAppProps(mockAppProps);
        mockServerToggleValue(toggleId, toggleServerUri, "false");
        assertThat(ToggleClient.isFeatureEnabled(toggleId)).isFalse();
    }

    private void mockAppProps(final List<String> appPropsList) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(String.join("\n", appPropsList).getBytes());
        BDDMockito.given(ConnectorUtils.getResourceAsStream(Mockito.anyString())).willReturn(inputStream);
    }

    private void mockServerToggleValue(final String toggleId, final String toggleServerUri, final String mockedValue) {
        Mockito.when(response.readEntity(String.class)).thenReturn(mockedValue);
        BDDMockito.given(ConnectorUtils.requestToggleValue(toggleId, toggleServerUri)).willReturn(response);
    }
}
