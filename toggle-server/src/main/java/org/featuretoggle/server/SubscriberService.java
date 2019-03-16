package org.featuretoggle.server;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SubscriberService {

    private Set<SseEmitter> emitters = new HashSet<>();

    private Set<SseEmitter> failedEmitters = new HashSet<>();

    public void addSubscriber(final SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void addBadSubscriber(final SseEmitter emitter) {
        failedEmitters.add(emitter);
    }

    public void sendMessageToSubsribers(final String msg) {
        if (!StringUtils.isEmpty(msg)) {
            log.info("Sending message to {} emitters: '{}'", emitters.size(), msg);
            emitters.forEach(emitter -> sendEmitterMessageBase64(emitter, msg));
            removeFailedEmitters();
        } else {
            log.warn("Did not send message to subscribers because it is empty: {}", msg);
        }
    }

    private void sendEmitterMessageBase64(final SseEmitter emitter, final String msg) {
        try {
            emitter.send(Base64.encodeBase64URLSafe(msg.getBytes()));
        } catch (Exception e) { // must catch any exception from send
            emitter.completeWithError(e);
        }
    }

    // removal must be done outside of forEach/iterator on emitters
    private void removeFailedEmitters() {
        int emitterPreCount = emitters.size();
        emitters.removeAll(failedEmitters);
        if (!failedEmitters.isEmpty()) {
            log.info("Removed closed emitters; emitters size: {} -> {}", emitterPreCount, emitters.size());
        }
        failedEmitters = new HashSet<>();
    }
}
