package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;

import java.io.IOException;

public class JsonClientImpl implements JsonClient {

    private final OTHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JsonClientImpl(OTHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = ObjectMapperFactory.create();
    }

    @Override
    public JsonNode get(String path, Object... parameters) {
        return httpClient.get(this::toJson, path, parameters);
    }

    private JsonNode toJson(String content) {
        JsonNode tree;
        try {
            tree = objectMapper.readTree(content);
        } catch (IOException e) {
            throw new JsonClientParsingException(e);
        }
        return tree;
    }
}
