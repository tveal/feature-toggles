package org.featuretoggle.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.here.oksse.ServerSentEvent;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@NoArgsConstructor
public class SseListener implements ServerSentEvent.Listener {

    @Override
    public void onOpen(final ServerSentEvent sse, final Response response) {
        log.info("[>> onOpen <<] response: {}", response);
    }

    @Override
    public void onMessage(final ServerSentEvent sse, final String id, final String event, final String message) {
        String decodedMessage = new String(Base64.decodeBase64(message));
        log.info("[>> onMessage <<] id: {}, event: {}, message: {}", id, event, decodedMessage);
        SseConnectionSingleton.addToggle(deserializeToggleMessage(decodedMessage));
    }

    @Override
    public void onComment(final ServerSentEvent sse, final String comment) {
        log.info("[>> onComment <<] comment: {}", comment);
    }

    @Override
    public boolean onRetryTime(final ServerSentEvent sse, final long milliseconds) {
        log.info("[>> onRetryTime <<] milliseconds: {}", milliseconds);
        return true;
    }

    @Override
    public boolean onRetryError(final ServerSentEvent sse, final Throwable throwable, final Response response) {
        log.info("[>> onRetryError <<] response: {}", response);
        return true;
    }

    @Override
    public void onClosed(final ServerSentEvent sse) {
        log.info("[>> onClosed <<]");
    }

    @Override
    public Request onPreRetry(final ServerSentEvent sse, final Request originalRequest) {
        log.info("[>> onPreRetry <<] originalRequest: {}", originalRequest);
        return originalRequest;
    }

    private Map<String, Boolean> deserializeToggleMessage(final String toggleMessage) {
        Map<String, Boolean> map = new HashMap<>();

        TypeReference<HashMap<String, Boolean>> typeRef = new TypeReference<HashMap<String, Boolean>>() {
        };
        try {
            map = new ObjectMapper().readValue(toggleMessage, typeRef);
        } catch (IOException e) {
            log.error("Failed to deserialize toggle server message; {}", toggleMessage, e);
        }
        return map;
    }

}
