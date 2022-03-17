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
     * Remote properties
     */
    var remote = GitRemoteConfigProperties()

    /**
     * Pull requests configuration
     */
    var pullRequests = GitPullRequestConfigProperties()

    /**
     * Indexation configuration
     */
    var indexation = GitIndexationConfigProperties()

    /**
     * Indexation properties
     */
    class GitIndexationConfigProperties {
        /**
         * Timeout for the Git indexations
         */
        @DurationUnit(ChronoUnit.MINUTES)
        var timeout: Duration = Duration.ofMinutes(30)
    }

    /**
     * General sync properties
     */
    class GitRemoteConfigProperties {
        /**
         * Timeout (by default in seconds) for a given remote operation to start (like fetch & clone)
         *
         * Leave 0 to use the default system value. Set to 60 seconds by default.
         *
         * This timeout is used for the _connection_ part, not the total duration of the operation.
         */
        @DurationUnit(ChronoUnit.SECONDS)
        var timeout: Duration = Duration.ofSeconds(60)
        /**
         * Timeout (by default in minutes) for a given remote operation to _complete_ (like fetch & clone)
         *
         * Set to 10 minutes by default.
         */
        @DurationUnit(ChronoUnit.SECONDS)
        var operationTimeout: Duration = Duration.ofMinutes(10)

        /**
         * Number of retries to run when there is a timeout.
         *
         * Set to 0 for no retry.
         */
        var retries: UInt = 3u

        /**
         * Interval between retries (by default in seconds and set to 30 seconds by default).
         */
        @DurationUnit(ChronoUnit.SECONDS)
        var interval: Duration = Duration.ofSeconds(30)

    }

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

        /**
         * Pull requests cleanup policy
         */
        var cleanup = GitPullRequestCleanupConfigProperties()
    }

    /**
     * Pull requests cleanup policy
     */
    class GitPullRequestCleanupConfigProperties() {
        /**
         * Cleanup enabled
         */
        var enabled: Boolean = true

        /**
         * Days before disabling
         */
        var disabling: Int = 1

        /**
         * Days after disabling, before deleting
         */
        var deleting: Int = 7
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
    }

}