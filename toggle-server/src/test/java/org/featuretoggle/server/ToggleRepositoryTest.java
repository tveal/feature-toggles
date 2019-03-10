package org.featuretoggle.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ToggleRepositoryTest {

    private static final Map<String, Boolean> TOGGLES = new HashMap<String, Boolean>() {
        private static final long serialVersionUID = -3680120605842154257L;

        {
            put("toggle1", Boolean.TRUE);
            put("toggle2", Boolean.FALSE);
            put("toggle3", Boolean.FALSE);
            put("my-feature", Boolean.TRUE);
        }
    };

    @Test
    public void derp() throws IOException {
        System.out.println(TOGGLES);

        String jsonResult = new ObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(TOGGLES);

        System.out.println(jsonResult);

        TypeReference<HashMap<String, Boolean>> typeRef = new TypeReference<HashMap<String, Boolean>>() {
        };
        Map<String, Boolean> map = new ObjectMapper().readValue(jsonResult, typeRef);

        System.out.println(map);
        Assertions.assertThat(map.get("my-feature").booleanValue()).isTrue();
    }
}
