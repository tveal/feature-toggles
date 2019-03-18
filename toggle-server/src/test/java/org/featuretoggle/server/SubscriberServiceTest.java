package org.featuretoggle.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RunWith(SpringJUnit4ClassRunner.class)
public class SubscriberServiceTest {

    SubscriberService service;

    Logger log = LogMocker.getMockLoggerForClass(SubscriberService.class);

    Set<SseEmitter> emitters;

    Set<SseEmitter> failedEmitters;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        service = new SubscriberService();
        emitters = getPrivateField(service, "emitters", Set.class);
        failedEmitters = getPrivateField(service, "failedEmitters", Set.class);
    }

    @Test
    public void sendMessageToSubsribers_shouldNotSendAndLogWarning_forEmptyMsg() throws Exception {
        service.createNewSubscriber();

        String msg = "";
        service.sendMessageToAllSubscribers(msg);

        verify(log, never()).info("Sending message to {} emitters: '{}'", emitters.size(), msg);
        verify(log).warn("Did not send message to subscribers because it is empty: {}", msg);
    }

    @Test
    public void sendMessageToSubsribers_shouldNotSendAndLogWarning_forNullMsg() throws Exception {
        service.createNewSubscriber();

        String msg = null;
        service.sendMessageToAllSubscribers(msg);

        verify(log, never()).info("Sending message to {} emitters: '{}'", emitters.size(), msg);
        verify(log).warn("Did not send message to subscribers because it is empty: {}", msg);
    }

    @Test
    public void sendMessageToSubsribers_shouldSendAndLog() throws Exception {
        service.createNewSubscriber(); // creates sub1
        SseEmitter sub2mock = mock(SseEmitter.class);
        emitters.add(sub2mock);

        String msg = "test";
        service.sendMessageToAllSubscribers(msg);

        assertThat(emitters).hasSize(2);
        assertThat(failedEmitters).hasSize(0);

        verify(log).info("Sending message to {} emitters: '{}'", 2, msg);
        verify(sub2mock).send(Base64.encodeBase64URLSafe(msg.getBytes()));
        verify(sub2mock, never()).complete();
    }

    @Test
    public void sendMessageToSubsribers_shouldCompleteWithError_forFailedEmitter() throws Exception {
        service.createNewSubscriber(); // creates sub1
        SseEmitter sub2mock = mock(SseEmitter.class);
        emitters.add(sub2mock);

        String msg = "test";
        byte[] encodedMsg = Base64.encodeBase64URLSafe(msg.getBytes());

        IOException ioException = new IOException("Some connection failure");
        doThrow(ioException).when(sub2mock).send(encodedMsg);
        doCallRealMethod().when(sub2mock).completeWithError(ioException);

        service.sendMessageToAllSubscribers(msg);

        assertThat(emitters).hasSize(1);
        assertThat(failedEmitters).hasSize(0);

        verify(log).info("Sending message to {} emitters: '{}'", 2, msg);
        verify(log).info("Removed closed emitters; emitters size: {} -> {}", 2, 1);
        verify(sub2mock).send(encodedMsg);
        verify(sub2mock).completeWithError(ioException);
    }

    public static <T> T getPrivateField(final Object obj, final String fieldName, final Class<T> fieldType)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field privateField = obj.getClass().getDeclaredField(fieldName);
        privateField.setAccessible(true);
        return fieldType.cast(privateField.get(obj));
    }
}
