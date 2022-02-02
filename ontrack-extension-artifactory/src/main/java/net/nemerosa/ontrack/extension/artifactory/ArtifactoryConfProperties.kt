package net.nemerosa.ontrack.extension.artifactory;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Artifactory extension configuration properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ontrack.extension.artifactory")
public class ArtifactoryConfProperties {

    /**
     * Disabling the build sync jobs?
     */
    boolean buildSyncDisabled;

}
