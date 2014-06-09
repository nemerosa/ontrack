package net.nemerosa.ontrack.client;

import org.springframework.stereotype.Component;

@Component
public class DefaultClientFactory implements ClientFactory {

    @Override
    public JsonClient getJsonClient(ClientConnection connection) {
        return new JsonClientImpl(getHttpClient(connection));
    }

    private HttpClient getHttpClient(ClientConnection connection) {
        return new HttpClientImpl(connection);
    }
}
