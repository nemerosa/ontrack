package net.nemerosa.ontrack.extension.artifactory.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ArtifactoryClient {

    List<String> getBuildNumbers(String buildName);

    JsonNode getBuildInfo(String buildName, String buildNumber);
}
