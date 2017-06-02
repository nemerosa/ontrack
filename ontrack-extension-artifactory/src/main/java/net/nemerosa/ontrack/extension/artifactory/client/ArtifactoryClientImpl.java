package net.nemerosa.ontrack.extension.artifactory.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.ClientNotFoundException;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtifactoryClientImpl implements ArtifactoryClient {

    private final JsonClient jsonClient;

    public ArtifactoryClientImpl(JsonClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    @Override
    public JsonClient getJsonClient() {
        return jsonClient;
    }

    @Override
    public JsonNode aql(String query) {
        return jsonClient.post(
                new StringEntity(
                        query,
                        ContentType.create("text/plain", "UTF-8")
                ),
                "/api/search/aql"
        );
    }

    @Override
    public List<String> getBuildNames() {
        JsonNode node = jsonClient.get("/api/build");
        List<String> names = new ArrayList<>();
        node.path("builds").forEach((JsonNode numberNode) -> {
            String name = StringUtils.stripStart(numberNode.path("uri").asText(), "/");
            if (StringUtils.isNotBlank(name)) {
                names.add(name);
            }
        });
        return names;
    }

    @Override
    public List<String> getBuildNumbers(String buildName) {
        try {
            JsonNode node = jsonClient.get("/api/build/%s", buildName);
            List<String> numbers = new ArrayList<>();
            node.path("buildsNumbers").forEach((JsonNode numberNode) -> {
                String number = StringUtils.stripStart(numberNode.path("uri").asText(), "/");
                if (StringUtils.isNotBlank(number)) {
                    numbers.add(number);
                }
            });
            return numbers;
        } catch (ClientNotFoundException ex) {
            // When the build is not defined, returns no build number
            return Collections.emptyList();
        }
    }

    @Override
    public JsonNode getBuildInfo(String buildName, String buildNumber) {
        return jsonClient.get("/api/build/%s/%s", buildName, buildNumber).path("buildInfo");
    }

    @Override
    public List<ArtifactoryStatus> getStatuses(JsonNode buildInfo) {
        List<ArtifactoryStatus> statuses = new ArrayList<>();
        buildInfo.path("statuses").forEach(statusNode -> statuses.add(new ArtifactoryStatus(
                statusNode.path("status").asText(),
                statusNode.path("user").asText(),
                LocalDateTime.parse(
                        statusNode.path("timestamp").asText(),
                        DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                        )
                )
        )));
        return statuses;
    }

}
