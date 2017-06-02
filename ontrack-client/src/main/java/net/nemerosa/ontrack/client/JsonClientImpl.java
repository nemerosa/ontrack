package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

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

    @Override
    public JsonNode delete(String path, Object... parameters) {
        return httpClient.delete(this::toJson, path, parameters);
    }

    @Override
    public JsonNode post(Object data, String path, Object... parameters) {
        try {
            return post(
                    new StringEntity(
                            objectMapper.writeValueAsString(data),
                            ContentType.create("application/json", "UTF-8")
                    ),
                    path,
                    parameters
            );
        } catch (JsonProcessingException e) {
            throw new JsonClientMappingException(e);
        }
    }

    @Override
    public JsonNode post(HttpEntity data, String path, Object... parameters) {
        return httpClient.post(
                this::toJson,
                data,
                path,
                parameters
        );
    }

    @Override
    public JsonNode put(Object data, String path, Object... parameters) {
        try {
            return httpClient.put(
                    this::toJson,
                    new StringEntity(
                            objectMapper.writeValueAsString(data),
                            ContentType.create("application/json", "UTF-8")
                    ),
                    path,
                    parameters
            );
        } catch (JsonProcessingException e) {
            throw new JsonClientMappingException(e);
        }
    }

    @Override
    public JsonNode upload(String name, Document o, String fileName, String path, Object... parameters) {
        return httpClient.upload(this::toJson, name, o, fileName, path, parameters);
    }

    @Override
    public Document download(String path, Object... parameters) {
        return httpClient.download(path, parameters);
    }

    @Override
    public String getUrl(String path, Object... parameters) {
        return httpClient.getUrl(path, parameters);
    }

    @Override
    public JsonNode toNode(Object data) {
        return objectMapper.valueToTree(data);
    }

    private JsonNode toJson(String content) {
        if (StringUtils.isBlank(content)) {
            return JsonUtils.object().end();
        } else {
            JsonNode tree;
            try {
                tree = objectMapper.readTree(content);
            } catch (IOException e) {
                throw new JsonClientParsingException(e);
            }
            return tree;
        }
    }
}
