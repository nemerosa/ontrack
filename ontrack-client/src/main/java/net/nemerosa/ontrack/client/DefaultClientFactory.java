package net.nemerosa.ontrack.client;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Component
public class DefaultClientFactory implements ClientFactory {

    @Override
    public JsonClient getJsonClient(ClientConnection connection) {
        return new JsonClientImpl(getHttpClient(connection));
    }

    private HttpClient getHttpClient(ClientConnection connection) {
        try {
            return new HttpClientImpl(connection);
        } catch (MalformedURLException e) {
            throw new ClientURLException(e);
        }
    }
}
