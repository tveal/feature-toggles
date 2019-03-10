package org.featuretoggle.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

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
@PrepareForTest({ RestUtils.class, FileUtils.class })
public class ToggleClientTest {

    @Mock
    Response response;

    @Before
    public void setup() {
        PowerMockito.mockStatic(RestUtils.class);
        PowerMockito.mockStatic(FileUtils.class);
    }

    @Test
    public void isFeatureEnabled_shouldLoadToggleValuesFromProps_orDefaultFalse() throws Exception {
        Properties props = new Properties();
        props.setProperty("toggle1", "false");
        props.setProperty("toggle2", "true");

        mockAppProps(props);
        assertThat(ToggleClient.isFeatureEnabled("toggle1")).isFalse();
        mockAppProps(props);
        assertThat(ToggleClient.isFeatureEnabled("toggle2")).isTrue();
        mockAppProps(props);
        assertThat(ToggleClient.isFeatureEnabled("toggle3")).isFalse();
    }

    @Test
    public void isFeatureEnabled_shouldReturnValueFromServer() throws Exception {
        String toggleServerUri = "fakehost:8090";
        String toggleId = "toggle1";

        Properties props = new Properties();
        props.setProperty(toggleId, "true");
        props.setProperty("toggleServerUri", toggleServerUri);

        mockAppProps(props);
        mockServerToggleValue(toggleId, toggleServerUri, "false");
        assertThat(ToggleClient.isFeatureEnabled(toggleId)).isFalse();
    }

    private void mockAppProps(final Properties props) throws Exception {
        BDDMockito.given(FileUtils.getAppProperties()).willReturn(props);
    }

    private void mockServerToggleValue(final String toggleId, final String toggleServerUri, final String mockedValue) {
        Mockito.when(response.readEntity(String.class)).thenReturn(mockedValue);
        BDDMockito.given(RestUtils.requestToggleValue(toggleId, toggleServerUri)).willReturn(response);
    }
}
