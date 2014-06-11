package net.nemerosa.ontrack.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.JsonClientImpl;
import net.nemerosa.ontrack.client.OTHttpClient;
import net.nemerosa.ontrack.client.OTHttpClientBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class to be inherited in order to create an acceptance test with some support
 * for the application fixtures.
 */
public abstract class AcceptanceSupport {

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

    private OTHttpClientBuilder clientBuilder() {
        return OTHttpClientBuilder.create(getBaseURL());
    }

    private String getBaseURL() {
        return env("ontrack.url", true, null, "Base URL for the application to test");
    }

    private Client client(Supplier<OTHttpClient> otHttpClientSupplier) {
        JsonClient jsonClient = new JsonClientImpl(otHttpClientSupplier.get());
        return (path, parameters) -> {
            JsonNode jsonNode = jsonClient.get(path, parameters);
            return consumer -> {
                consumer.accept(jsonNode);
                return consumer;
            };
        };
    }

    protected static interface Client {

        JsonResult get(String path, Object... parameters);

    }

    protected static interface JsonResult {

        Consumer<JsonNode> with(Consumer<JsonNode> consumer);

    }

}
