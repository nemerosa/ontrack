package net.nemerosa.ontrack.extension.artifactory.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;

@Data
public class ArtifactoryPromotionSyncProperty implements ConfigurationProperty<ArtifactoryConfiguration> {

    /**
     * Reference to the Artifactory configuration.
     */
    private final ArtifactoryConfiguration configuration;

    /**
     * Artifactory build name
     */
    private final String buildName;

    /**
     * Artifactory build name filter
     */
    private final String buildNameFilter;

    /**
     * Interval between each synchronisation in minutes.
     */
    private final int interval;

}
