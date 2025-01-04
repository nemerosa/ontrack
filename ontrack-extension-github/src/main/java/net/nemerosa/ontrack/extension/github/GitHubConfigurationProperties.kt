package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = GitHubConfigurationProperties.PREFIX)
@APIName("GitHub configuration")
@APIDescription("Configuration of the GitHub extension")
class GitHubConfigurationProperties {

    var metrics = Metrics()

    class Metrics {
        @APIDescription("Set to `false` to disable the export of the GitHub API rate limit")
        var enabled: Boolean = true
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.github"
    }
}