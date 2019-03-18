package org.featuretoggle.server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ApiController implements ErrorController {

    private static final String ERR_PATH = "/error";

    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private ToggleRepository toggleRepo;

    /**
     * Sample url: http://localhost:8090/toggle-server/get-toggle?toggleId=feature%2FPROJ-1234
     */
    @RequestMapping("/get-toggle")
    public String getToggle(@RequestParam("toggleId") final String toggleId) {
        String returnValue = "false";

        try {
            String decodedToggleId = URLDecoder.decode(toggleId, StandardCharsets.UTF_8.name());
            Boolean toggleValue = toggleRepo.retrieveToggleValue(decodedToggleId);
            if (toggleValue != null) {
                returnValue = toggleValue.toString();
            }
        } catch (Exception e) {
            returnValue = "Invalid toggleId";
            log.error("Something failed looking up toggleId: {};", toggleId, e);
        }
        return returnValue;
    }

    @RequestMapping(ERR_PATH)
    public String error() {
        return "Oops! What you're looking for cannot be found. <br />"
                + "If running with 'bootRun', only the backend stuff works (no UI).";
    }

    @Override
    public String getErrorPath() {
        return ERR_PATH;
    }

    // https://howtodoinjava.com/spring-boot2/spring-async-controller-sseemitter/
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/sse")
    public SseEmitter sseNewSubscriber() {
        SseEmitter emitter = subscriberService.createNewSubscriber();
        nonBlockingService.execute(() -> {
            try {
                subscriberService.sendMessageToOneSubscriber(emitter, toggleRepo.retrieveAllToggles());
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
    public String switchToggle(@RequestParam("toggleId") final String toggleId) {
        String msg = toggleRepo.switchToggle(toggleId);

        subscriberService.sendMessageToAllSubscribers(msg);
        return msg;
    }
}
