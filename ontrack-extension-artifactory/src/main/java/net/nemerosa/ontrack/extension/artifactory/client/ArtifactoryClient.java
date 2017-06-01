package net.nemerosa.ontrack.extension.artifactory.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;

import java.util.List;

public interface ArtifactoryClient {

    List<String> getBuildNames();

    List<String> getBuildNumbers(String buildName);

    JsonNode getBuildInfo(String buildName, String buildNumber);

    List<ArtifactoryStatus> getStatuses(JsonNode buildInfo);

    /**
     * Access to the underlying JSON client
     */
    JsonClient getJsonClient();
}
