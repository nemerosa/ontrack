package net.nemerosa.ontrack.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.client.*;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class to be inherited in order to create an acceptance test with some support
 * for the application fixtures.
 */
public abstract class AcceptanceSupport {

    private static final ObjectMapper mapper = ObjectMapperFactory.create();

    /**
     * Waits for the application to be ready by performing a call
     */
    @Before
    public void waitForApplication() {
        System.out.format("Check for application readiness at %s%n", getBaseURL());
    }

    public JsonNode nameDescription() {
        return object()
                .with("name", uid(""))
                .with("description", uid(""))
                .end();
    }

    @Deprecated
    public static String env(String property, boolean required, String defaultValue, String name) {
        String sys = System.getProperty(property);
        if (StringUtils.isNotBlank(sys)) {
            return sys;
        } else {
            String envName = property.toUpperCase().replace(".", "_");
            String env = System.getenv(envName);
            if (StringUtils.isNotBlank(env)) {
                return env;
            } else if (required) {
                throw new IllegalStateException(
                        String.format(
                                "The %s system property or %s environment variable is required (%s)",
                                property,
                                envName,
                                name
                        )
                );
            } else {
                return defaultValue;
            }
        }
    }

    protected Client anonymous() {
        return client(() -> clientBuilder().build());
    }

    protected Client admin() {
        String adminPassword = getAdminPassword();
        return client(() -> clientBuilder().withCredentials("admin", adminPassword).build());
    }

    protected String getAdminPassword() {
        return env("ontrack.admin", false, "admin", "Acceptance admin password");
    }

    private OTHttpClientBuilder clientBuilder() {
        boolean disableSsl = isSslDisabled();
        return OTHttpClientBuilder.create(getBaseURL(), disableSsl);
    }

    protected boolean isSslDisabled() {
        return "true".equals(env("ontrack.disableSsl", false, "false", "Disabling SSL checks"));
    }

    protected String getBaseURL() {
        return env("ontrack.url", true, null, "Base URL for the application to test");
    }

    private Client client(Supplier<OTHttpClient> otHttpClientSupplier) {
        JsonClient jsonClient = new JsonClientImpl(otHttpClientSupplier.get());
        return new Client() {

            @Override
            public JsonResult get(String path, Object... parameters) {
                return new SimpleJsonResult(jsonClient.get(path, parameters));
            }

            @Override
            public JsonResult delete(String path, Object... parameters) {
                return new SimpleJsonResult(jsonClient.delete(path, parameters));
            }

            @Override
            public JsonResult post(Object data, String path, Object... parameters) {
                return new SimpleJsonResult(jsonClient.post(data, path, parameters));
            }

            @Override
            public JsonResult put(Object data, String path, Object... parameters) {
                return new SimpleJsonResult(jsonClient.put(data, path, parameters));
            }
        };
    }

    protected void validationMessage(Runnable task, String expectedMessage) throws IOException {
        try {
            task.run();
            fail("The call should have failed with a validation exception.");
        } catch (ClientValidationException ex) {
            JsonNode error = mapper.readTree(ex.getMessage());
            assertEquals(
                    expectedMessage,
                    error.path("message").asText()
            );
        }
    }

    protected interface Client {

        JsonResult get(String path, Object... parameters);

        JsonResult delete(String path, Object... parameters);

        JsonResult post(Object data, String path, Object... parameters);

        JsonResult put(Object data, String path, Object... parameters);

    }

    protected interface JsonResult {

        JsonNode get();

        Consumer<JsonNode> withNode(Consumer<JsonNode> consumer);

    }

    protected static class SimpleJsonResult implements JsonResult {

        private final JsonNode node;

        public SimpleJsonResult(JsonNode node) {
            this.node = node;
        }

        @Override
        public JsonNode get() {
            return node;
        }

        @Override
        public Consumer<JsonNode> withNode(Consumer<JsonNode> consumer) {
            consumer.accept(node);
            return consumer;
        }
    }

}
