package net.nemerosa.ontrack.extension.artifactory.client;

import java.util.List;

public interface ArtifactoryClient {

    List<String> getBuildNumbers(String buildName);

}
