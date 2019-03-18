package org.featuretoggle.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ApiController.class, secure = false)
public class ApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    SubscriberService subscriberService;

    @MockBean
    ToggleRepository toggleRepo;

    Logger log = LogMocker.getMockLoggerForClass(ApiController.class);

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

    @Test
    public void getErrorPath() {
        ApiController api = new ApiController();

        assertThat(api.getErrorPath()).isEqualTo("/error");
    }

    @Test
    public void sseNewSubscriber_shouldSendToggleMessage_toNewSubscriptionEmitter() throws Exception {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        String toggleMsg = "{ \"my-feature\": true }";

        when(subscriberService.createNewSubscriber()).thenReturn(mockEmitter);
        when(toggleRepo.retrieveAllToggles()).thenReturn(toggleMsg);

        getMvcResult("/sse");

        verify(subscriberService).sendMessageToOneSubscriber(mockEmitter, toggleMsg);
        verify(log, never()).error(Mockito.eq("Failed to send emitter message;"), Mockito.any(Throwable.class));
        verify(mockEmitter, never()).completeWithError(Mockito.any());
    }

    @Test
    public void sseNewSubscriber_shouldLogAndCompleteWithError_forFailedSend() throws Exception {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        String toggleMsg = "{ \"my-feature\": true }";
        NullPointerException npe = new NullPointerException("send exception");

        when(subscriberService.createNewSubscriber()).thenReturn(mockEmitter);
        when(toggleRepo.retrieveAllToggles()).thenThrow(npe);

        getMvcResult("/sse");

        verify(subscriberService, never()).sendMessageToOneSubscriber(mockEmitter, toggleMsg);
        verify(log).error(Mockito.eq("Failed to send emitter message;"), Mockito.any(Throwable.class));
        verify(mockEmitter).completeWithError(npe);
    }

    @Test
    public void switchToggle_shouldChangeToggle_andPublishToAllSubscribers() throws Exception {
        String toggleId = "my-cool-feature";
        String toggleMsg = "{ \"my-cool-feature\": true }";

        when(toggleRepo.switchToggle(toggleId)).thenReturn(toggleMsg);

        MvcResult result = getMvcResult("/switch?toggleId=" + toggleId);

        verify(subscriberService).sendMessageToAllSubscribers(toggleMsg);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(toggleMsg);
    }

    private MvcResult getMvcResult(final String restPath) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(restPath).accept(MediaType.TEXT_PLAIN)).andReturn();
    }
}
