package net.nemerosa.ontrack.client;

public class JsonClientImpl implements JsonClient {

    private final HttpClient httpClient;

    public JsonClientImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

}
