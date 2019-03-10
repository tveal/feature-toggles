package org.featuretoggle.client;

import org.featuretoggle.client.ToggleRetriever;

public class SseToggleRetriever extends ToggleRetriever {
    @Override
    protected Boolean getToggleFromServer(final String toggleId) {
        return SseConnectionSingleton.getToggle(toggleId);
    }
}
