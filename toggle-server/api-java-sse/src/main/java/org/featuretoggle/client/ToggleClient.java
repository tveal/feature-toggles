package org.featuretoggle.client;

public class ToggleClient {

    public static boolean isFeatureEnabled(final String toggleId) {
        return new SseToggleRetriever().getToggleValueOrDefault(toggleId);
    }
}
