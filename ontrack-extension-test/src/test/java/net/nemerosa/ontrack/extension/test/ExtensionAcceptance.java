package net.nemerosa.ontrack.extension.test;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.JsonClientImpl;
import net.nemerosa.ontrack.client.OTHttpClient;
import net.nemerosa.ontrack.client.OTHttpClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExtensionAcceptance {

    private static String baseUrl;

    @BeforeClass
    public static void init() {
        baseUrl = getEnvironment(
                "ontrack.url",
                "ONTRACK_URL",
                "http://localhost:8080"
        );
        System.out.println("[extension][test] Starting running acceptance tests...");
        System.out.println("[extension][test] Base URL = " + baseUrl);
    }

    private static String getEnvironment(String systemProperty, String environmentVariable, String defaultValue) {
        String env = System.getenv(environmentVariable);
        String sys = System.getProperty(systemProperty);
        if (StringUtils.isNotBlank(sys)) {
            return sys;
        } else if (StringUtils.isNotBlank(env)) {
            return env;
        } else {
            return defaultValue;
        }
    }

    @Test
    public void information_is_accessible() {
        OTHttpClient httpClient = OTHttpClientBuilder.create(baseUrl, true)
                .withLogger(System.out::println)
                .build();
        JsonClient client = new JsonClientImpl(httpClient);
        JsonNode info = client.get("info");
        String displayVersion = info.path("version").path("display").asText();
        assertTrue(StringUtils.isNotBlank(displayVersion));
    }

}
