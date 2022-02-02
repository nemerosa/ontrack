package net.nemerosa.ontrack.extension.artifactory

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Configuration properties for the Artifactory extension configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "ontrack.extension.artifactory")
class ArtifactoryConfProperties {
    /**
     * Disabling the build sync jobs?
     */
    var buildSyncDisabled = false
}