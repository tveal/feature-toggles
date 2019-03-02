package client;

import org.junit.Test;

public class ToggleClientTest {

    @Test
    public void isFeatureEnabled() {
        String toggle1 = "toggle1";
        String toggle2 = "toggle2";
        System.out.println(String.join(" ", ">>>", toggle1, ToggleClient.isFeatureEnabled(toggle1)));
        System.out.println(String.join(" ", ">>>", toggle2, ToggleClient.isFeatureEnabled(toggle2)));
    }
}
