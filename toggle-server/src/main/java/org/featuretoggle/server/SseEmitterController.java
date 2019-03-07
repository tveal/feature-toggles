package org.featuretoggle.server;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SseEmitterController {

    private static final long ONE_DAY_MILLIS = 86400000L;

    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    private Set<SseEmitter> emitters = new HashSet<>();

    @GetMapping("/sse")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter(ONE_DAY_MILLIS);
        emitter.onCompletion(() -> {
            int emitterPreCount = emitters.size();
            emitters.remove(emitter);
            log.info("Removing emitter; emitters size: {} -> {}", emitterPreCount, emitters.size());
        });
        emitters.add(emitter);

        nonBlockingService.execute(() -> {
            try {
                String emitterMsg = "/sse @ " + new Date();
                log.info("Sending emitter message: {}", emitterMsg);
                emitter.send(emitterMsg);
            } catch (Exception e) {
                log.error("Failed to send emitter message;", e);
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/publish")
    public String publishMessage() {
        String msg = "/publish @ " + new Date();
        log.info("Sending message to {} emitters: '{}'", emitters.size(), msg);
        emitters.forEach(emitter -> {
            try {
                emitter.send(msg);
            } catch (Exception e) {
                log.error("Failed to send emitter message;", e);
                emitter.completeWithError(e);
            }
        });
        return msg;
    }
}
