package net.nemerosa.ontrack.extension.artifactory.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
