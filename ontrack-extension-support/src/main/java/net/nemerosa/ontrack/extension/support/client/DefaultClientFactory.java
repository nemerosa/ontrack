package net.nemerosa.ontrack.extension.support.client;

import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.JsonClientImpl;
import net.nemerosa.ontrack.client.OTHttpClient;
import net.nemerosa.ontrack.client.OTHttpClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefaultClientFactory implements ClientFactory {

    @Override
    public JsonClient getJsonClient(ClientConnection clientConnection) {
        // Gets a HTTP client
        OTHttpClient httpClient = getHttpClient(clientConnection);
        // Builds a JSON client on top of it
        return new JsonClientImpl(httpClient);
    }

    @Override
    public OTHttpClient getHttpClient(ClientConnection clientConnection) {
        return OTHttpClientBuilder.create(clientConnection.getUrl(), false)
                // Basic credentials
                .withCredentials(clientConnection.getUser(), clientConnection.getPassword())
                        // OK
                .build();
    }
}
