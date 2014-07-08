package net.nemerosa.ontrack.extension.artifactory.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ArtifactoryClientImpl implements ArtifactoryClient {

    private final JsonClient jsonClient;

    public ArtifactoryClientImpl(JsonClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    @Override
    public List<String> getBuildNumbers(String buildName) {
        JsonNode node = jsonClient.get("/api/build/%s", buildName);
        List<String> numbers = new ArrayList<>();
        node.path("buildsNumbers").forEach((JsonNode numberNode) -> {
            String number = StringUtils.stripStart(numberNode.path("uri").asText(), "/");
            if (StringUtils.isNotBlank(number)) {
                numbers.add(number);
            }
        });
        return numbers;
    }

    @Override
    public JsonNode getBuildInfo(String buildName, String buildNumber) {
        return jsonClient.get("/api/build/%s/%s", buildName, buildNumber).path("buildInfo");
    }

}
