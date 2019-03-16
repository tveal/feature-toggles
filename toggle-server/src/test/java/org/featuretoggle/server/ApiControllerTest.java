package org.featuretoggle.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ApiController.class, secure = false)
public class ApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ToggleRepository toggleRepo;

    @Test
    public void error_shouldReturnErrorMsg() throws Exception {
        MvcResult result = getMvcResult("/error");

        assertThat(result.getResponse().getContentAsString())
            .isEqualTo("Oops! What you're looking for cannot be found. <br />"
                    + "If running with 'bootRun', only the backend stuff works (no UI).");
    }

    @Test
    public void getToggle_shouldReturnTrueString_forEnabledToggle() throws Exception {
        String toggleId = "my-feature-toggle";

        when(toggleRepo.retrieveToggleValue(toggleId)).thenReturn(new Boolean(true));
        MvcResult result = getMvcResult("/get-toggle?toggleId=" + toggleId);

        assertThat(result.getResponse().getContentAsString()).isEqualTo("true");
    }

    @Test
    public void getToggle_shouldReturnFalseString_forDisabledToggle() throws Exception {
        String toggleId = "my-feature-toggle";

        when(toggleRepo.retrieveToggleValue(toggleId)).thenReturn(new Boolean(false));
        MvcResult result = getMvcResult("/get-toggle?toggleId=" + toggleId);

        assertThat(result.getResponse().getContentAsString()).isEqualTo("false");
    }

    @Test
    public void getToggle_shouldReturnFalseString_forNullToggle() throws Exception {
        String toggleId = "my-feature-toggle";

        when(toggleRepo.retrieveToggleValue(toggleId)).thenReturn(null);
        MvcResult result = getMvcResult("/get-toggle?toggleId=" + toggleId);

        assertThat(result.getResponse().getContentAsString()).isEqualTo("false");
    }

    @Test
    public void getToggle_shouldReturnInvalidToggleMessage_forBadToggleId() throws Exception {
        String toggleId = "kjshf(^%%^&";

        when(toggleRepo.retrieveToggleValue(toggleId)).thenReturn(null);
        MvcResult result = getMvcResult("/get-toggle?toggleId=" + toggleId);

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Invalid toggleId");
    }

    private MvcResult getMvcResult(final String restPath) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(restPath).accept(MediaType.TEXT_PLAIN)).andReturn();
    }
}
