package org.featuretoggle.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
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
public class SseController {

    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private ToggleRepository toggleRepo;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/sse")
    public SseEmitter handleSse() {
        SseEmitter emitter = subscriberService.createNewSubscriber();

        nonBlockingService.execute(() -> {
            try {
                String msg = toggleRepo.retrieveAllToggles();
                subscriberService.sendMessageToSubsribers(msg);
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
        String msg = toggleRepo.switchToggle(toggleId);

        subscriberService.sendMessageToSubsribers(msg);
        return msg;
    }

}
