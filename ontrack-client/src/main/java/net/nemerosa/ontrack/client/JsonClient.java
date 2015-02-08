package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.Document;

public interface JsonClient {

    JsonNode toNode(Object data);

    JsonNode get(String path, Object... parameters);

    JsonNode delete(String path, Object... parameters);

    JsonNode post(Object data, String path, Object... parameters);

    JsonNode put(Object data, String path, Object... parameters);

    JsonNode upload(String name, Document o, String fileName, String path, Object... parameters);

    Document download(String path, Object... parameters);
}
