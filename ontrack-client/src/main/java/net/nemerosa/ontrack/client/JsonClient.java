package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.Document;
import org.apache.http.HttpEntity;

public interface JsonClient {

    JsonNode toNode(Object data);

    JsonNode get(String path, Object... parameters);

    JsonNode delete(String path, Object... parameters);

    JsonNode post(Object data, String path, Object... parameters);

    /**
     * General POST method
     *
     * @param data       Entity to post
     * @param path       Relative path
     * @param parameters Parameter for the relative path parameters (if any)
     * @return Result as JSON
     */
    JsonNode post(HttpEntity data, String path, Object... parameters);

    JsonNode put(Object data, String path, Object... parameters);

    JsonNode upload(String name, Document o, String fileName, String path, Object... parameters);

    Document download(String path, Object... parameters);

    /**
     * Gets the URL for a relative path
     *
     * @param path       Relative path
     * @param parameters Parameter for the relative path parameters (if any)
     * @return Absolute URL
     */
    String getUrl(String path, Object... parameters);
}
