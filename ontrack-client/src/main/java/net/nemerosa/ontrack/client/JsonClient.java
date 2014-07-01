package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonClient {

    JsonNode get(String path, Object... parameters);

    JsonNode post(JsonNode data, String path, Object... parameters);

}
