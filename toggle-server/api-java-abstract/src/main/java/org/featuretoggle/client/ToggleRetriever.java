package org.featuretoggle.client;

import static org.featuretoggle.client.FileUtils.APP_PROPS;
import static org.featuretoggle.client.FileUtils.getAppProperties;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public abstract class ToggleRetriever {

    protected abstract Boolean getToggleFromServer(String toggleId);

    public boolean getToggleValueOrDefault(final String toggleId) {
        Boolean serverToggleValue = getToggleFromServer(toggleId);
        boolean isToggleEnabled = false;

        if (serverToggleValue != null) {
            isToggleEnabled = serverToggleValue.booleanValue();
        } else {
            log.error("Failed to retrieve toggle value from toggle-server, checking {} for {};", APP_PROPS, toggleId);
            isToggleEnabled = getToggleValueFromProps(toggleId);
        }
        return isToggleEnabled;
    }

    private boolean getToggleValueFromProps(final String toggleId) {
        String togglePropValue = getAppProperties().getProperty(toggleId);
        boolean boolValue = false;
        if (togglePropValue != null) {
            log.info("Found {} value: {}, from {}", toggleId, togglePropValue, APP_PROPS);
            boolValue = Boolean.parseBoolean(togglePropValue);
        } else {
            log.error("{} not found in {}, defaulting to false", toggleId, APP_PROPS);
        }
        return boolValue;
    }
}
