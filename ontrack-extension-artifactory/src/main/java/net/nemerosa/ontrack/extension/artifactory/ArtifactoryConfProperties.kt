package net.nemerosa.ontrack.extension.artifactory

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Configuration properties for the Artifactory extension configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "ontrack.extension.artifactory")
@APIName("Artifactory configuration")
@APIDescription("Configuration of the Artifactory extension")
class ArtifactoryConfProperties {
    @APIDescription("Disabling the build sync jobs?")
    var buildSyncDisabled = false
}