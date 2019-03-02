package hello;

import java.util.ArrayList;
import java.util.List;

import org.featuretoggle.client.ToggleClient;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController implements ErrorController {

    private static final String ERR_PATH = "/error";

    @RequestMapping("/")
    public String api() {
        String featureToggle = "my-feature";
        List<String> lines = new ArrayList<>();

        if (ToggleClient.isFeatureEnabled(featureToggle)) {
            lines.add(String.format("Shazzam!!! featureToggle %s is ENABLED :D", featureToggle));
        } else {
            lines.add(String.format("Welst... featureToggle '%s' is disabled :(", featureToggle));
        }

        return String.join("<br />", lines);
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
