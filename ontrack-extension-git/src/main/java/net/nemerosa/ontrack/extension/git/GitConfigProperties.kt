package net.nemerosa.ontrack.extension.git

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Static configuration for the Git extension.
 */
@Component
@ConfigurationProperties(prefix = "ontrack.config.extension.git")
class GitConfigProperties {

    /**
     * Enabling pull requests
     */
    var pullRequests: Boolean = true

}