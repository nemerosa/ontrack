package net.nemerosa.ontrack.extension.git

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Static configuration for the Git extension.
 */
@Component
@ConfigurationProperties(prefix = "ontrack.config.extension.git")
class GitConfigProperties {

    /**
     * Pull requests configuration
     */
    var pullRequests = GitPullRequestConfigProperties()

    /**
     * Pull requests configuration
     */
    class GitPullRequestConfigProperties {
        /**
         * Are pull requests enabled?
         */
        var enabled: Boolean = true

        /**
         * Pull requests caching configuration
         */
        var cache = GitPullRequestCacheConfigProperties()
    }

    /**
     * Pull requests caching configuration
     */
    class GitPullRequestCacheConfigProperties {
        /**
         * Is the cache for pull requests enabled?
         */
        var enabled: Boolean = true

        /**
         * Duration for the cache
         */
        @DurationUnit(ChronoUnit.MINUTES)
        var duration: Duration = Duration.ofMinutes(30)

        /**
         * Size of the cache
         */
        var size: Int = 200
    }

}