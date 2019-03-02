package hello;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController implements ErrorController {

    private static final String ERR_PATH = "/error";

    private static final Map<String, Boolean> toggles = new HashMap<String, Boolean>() {
        {
            put("toggle1", true);
            put("toggle2", false);
        }
    };

    /**
     * Sample url:
     * http://localhost:8090/toggle-server/get-toggle?toggleId=feature%2FPROJ-1234
     */
    @RequestMapping("/get-toggle")
    public String getToggle(@RequestParam("toggleId") String toggleId) {
        String returnValue;
        
        try {
            String decodedToggleId = URLDecoder.decode(toggleId, StandardCharsets.UTF_8.name());
            Boolean toggleValue = toggles.get(decodedToggleId);
            if (toggleValue != null) {
                returnValue = "Toggle value=" + toggleValue.booleanValue();
            } else {
                returnValue = "You requested value for toggleId: " + decodedToggleId;
            }
        } catch(UnsupportedEncodingException e) {
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
