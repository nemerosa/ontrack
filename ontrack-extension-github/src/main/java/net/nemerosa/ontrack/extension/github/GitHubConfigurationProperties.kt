package net.nemerosa.ontrack.extension.github

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = GitHubConfigurationProperties.PREFIX)
class GitHubConfigurationProperties {

    var metrics = Metrics()

    class Metrics {
        var enabled: Boolean = true
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.github"
    }
}