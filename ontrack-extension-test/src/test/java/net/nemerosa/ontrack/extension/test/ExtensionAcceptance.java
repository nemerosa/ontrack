package net.nemerosa.ontrack.extension.test;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.acceptance.config.AcceptanceConfig;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.JsonClientImpl;
import net.nemerosa.ontrack.client.OTHttpClient;
import net.nemerosa.ontrack.client.OTHttpClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExtensionAcceptance {

    private static String baseUrl;
    private static String version;

    @BeforeClass
    public static void init() throws InterruptedException, ExecutionException, TimeoutException {
        baseUrl = getEnvironment(
                "ontrack.url",
                "ONTRACK_URL",
                "http://localhost:8080"
        );
        version = getEnvironment(
                "ontrack.version",
                "ONTRACK_VERSION",
                "n/a"
        );
        System.out.println("[extension][test] Starting running acceptance tests...");
        System.out.println("[extension][test] Base URL = " + baseUrl);
        System.out.println("[extension][test] Version = " + version);
        System.out.println("[extension][test] Waiting for Ontrack to be ready...");
        AcceptanceConfig acceptanceConfig = new AcceptanceConfig();
        acceptanceConfig.setUrl(baseUrl);
        acceptanceConfig.check();
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
        JsonClient client = getJsonClient();
        JsonNode info = client.get("info");
        String displayVersion = info.path("version").path("display").asText();
        assertTrue(StringUtils.isNotBlank(displayVersion));
    }

    @Test
    public void version_check() {
        JsonClient client = getJsonClient();
        JsonNode info = client.get("info");
        String displayVersion = info.path("version").path("display").asText();
        assertEquals(version, displayVersion);
    }

    @Test
    public void extension_loaded() {
        JsonClient client = getJsonClient();
        JsonNode extensionList = client.get("extensions");
        boolean found = false;
        for (JsonNode extensionFeatureDescription : extensionList.path("extensions")) {
            if ("test".equals(extensionFeatureDescription.path("id").asText())) {
                assertEquals("Test", extensionFeatureDescription.path("name").asText());
                assertEquals("Test extension", extensionFeatureDescription.path("description").asText());
                found = true;
            }
        }
        assertTrue("Test extension must be present", found);
    }

    private JsonClient getJsonClient() {
        OTHttpClient httpClient = OTHttpClientBuilder.create(baseUrl, true)
                .withLogger(System.out::println)
                .build();
        return new JsonClientImpl(httpClient);
    }

}
