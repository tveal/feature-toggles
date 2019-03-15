package org.featuretoggle.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SseEmitterController {

    private static final long ONE_DAY_MILLIS = 86400000L;

    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    private Set<SseEmitter> emitters = new HashSet<>();

    private Set<SseEmitter> failedEmitters = new HashSet<>();

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/sse")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter(ONE_DAY_MILLIS);
        emitter.onCompletion(() -> {
            failedEmitters.add(emitter);
        });
        emitters.add(emitter);

        nonBlockingService.execute(() -> {
            try {
                String msg = ToggleRepository.retrieveAllToggles();
                if (!StringUtils.isEmpty(msg)) {
                    log.info("Sending emitter message: {}", msg);
                    sendEmitterMessageBase64(emitter, msg);
                    removeFailedEmitters();
                }
            } catch (Exception e) {
                log.error("Failed to send emitter message;", e);
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /**
     * http://localhost:8090/toggle-server/switch?toggleId=my-feature
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping("/switch")
    public String publishMessage(@RequestParam("toggleId") final String toggleId) {
        String msg = ToggleRepository.switchToggle(toggleId);

        if (!StringUtils.isEmpty(msg)) {
            log.info("Sending message to {} emitters: '{}'", emitters.size(), msg);
            emitters.forEach(emitter -> sendEmitterMessageBase64(emitter, msg));
            removeFailedEmitters();
        }
        return msg;
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
