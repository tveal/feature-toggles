package org.featuretoggle.client;

import static org.featuretoggle.client.FileUtils.APP_PROPS;
import static org.featuretoggle.client.FileUtils.getAppProperties;

import java.util.HashMap;
import java.util.Map;

import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

// https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
@Slf4j
public class SseConnectionSingleton {

    private Map<String, Boolean> inMemoryToggles = new HashMap<>();

    private SseConnectionSingleton() {
        String toggleServerUri = getAppProperties().getProperty("toggleServerUri");
        if (toggleServerUri != null) {
            // https://github.com/heremaps/oksse
            Request request = new Request.Builder().url(toggleServerUri + "/toggle-server/sse").build();
            OkSse okSse = new OkSse();
            ServerSentEvent sse = okSse.newServerSentEvent(request, new SseListener());
        } else {
            log.error("toggleServerUri not found in {}", APP_PROPS);
        }
    }

    private static class SingletonHelper {
        private static final SseConnectionSingleton INSTANCE = new SseConnectionSingleton();
    }

    private static SseConnectionSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public static void addToggle(final Map<String, Boolean> toggleMap) {
        getInstance().inMemoryToggles.putAll(toggleMap);
    }

    public static Boolean getToggle(final String toggleId) {
        return getInstance().inMemoryToggles.get(toggleId);
    }
}
