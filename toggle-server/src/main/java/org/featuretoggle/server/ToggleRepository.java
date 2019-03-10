package org.featuretoggle.server;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToggleRepository {

    private static final Map<String, Boolean> TOGGLES = new HashMap<String, Boolean>() {
        private static final long serialVersionUID = -3680120605842154257L;

        {
            put("toggle1", Boolean.TRUE);
            put("toggle2", Boolean.FALSE);
            put("toggle3", Boolean.FALSE);
            put("my-feature", Boolean.TRUE);
        }
    };

    public static String retrieveAllToggles() {
        return serializeToggleMap(TOGGLES);
    }

    public static Boolean retrieveToggleValue(final String toggleId) {
        return TOGGLES.get(toggleId);
    }

    public static String switchToggle(final String toggleId) {
        Boolean currentToggle = TOGGLES.get(toggleId);
        if (currentToggle.booleanValue()) {
            TOGGLES.put(toggleId, Boolean.FALSE);
        } else {
            TOGGLES.put(toggleId, Boolean.TRUE);
        }
        Map<String, Boolean> changedToggleMap = new HashMap<>();
        changedToggleMap.put(toggleId, retrieveToggleValue(toggleId));
        return serializeToggleMap(changedToggleMap);
    }

    private static String serializeToggleMap(final Map<String, Boolean> toggleMap) {
        String mapString = null;
        try {
            mapString = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(toggleMap);
        } catch (JsonProcessingException e) {
            log.error("Failed serializing toggles to String", e);
        }
        return mapString;
    }

}
