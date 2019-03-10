package org.featuretoggle.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController implements ErrorController {

    private static final String ERR_PATH = "/error";

    /**
     * Sample url: http://localhost:8090/toggle-server/get-toggle?toggleId=feature%2FPROJ-1234
     */
    @RequestMapping("/get-toggle")
    public String getToggle(@RequestParam("toggleId") final String toggleId) {
        String returnValue = "false";

        try {
            String decodedToggleId = URLDecoder.decode(toggleId, StandardCharsets.UTF_8.name());
            Boolean toggleValue = ToggleRepository.retrieveToggleValue(decodedToggleId);
            if (toggleValue != null) {
                returnValue = toggleValue.toString();
            } else {
                returnValue = "You requested value for toggleId: " + decodedToggleId;
            }
        } catch (UnsupportedEncodingException e) {
            returnValue = "Invalid toggleId";
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
}
