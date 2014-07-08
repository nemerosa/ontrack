package net.nemerosa.ontrack.extension.artifactory.client;

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;

public interface ArtifactoryClientFactory {

    ArtifactoryClient getClient(ArtifactoryConfiguration configuration);

}
