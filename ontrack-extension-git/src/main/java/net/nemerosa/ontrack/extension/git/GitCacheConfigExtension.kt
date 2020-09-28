package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.api.CacheConfigExtension
import org.springframework.stereotype.Component

/**
 * Configuration of caching for the Git module
 */
@Component
class GitCacheConfigExtension(
        gitConfigProperties: GitConfigProperties
) : CacheConfigExtension {
    override val caches: Map<String, String> = mapOf(
            CACHE_GIT_CHANGE_LOG to "maximumSize=20,expireAfterWrite=10m,recordStats",
            // Cache for pull requests
            CACHE_GIT_PULL_REQUEST to "maximumSize=${gitConfigProperties.pullRequests.cache.size},expireAfterWrite=${gitConfigProperties.pullRequests.cache.duration.toMinutes()}m,recordStats"
    )
}

/**
 * Cache key for pull requests
 */
const val CACHE_GIT_PULL_REQUEST = "gitPullRequests"

/**
 * Throw when the cache for PRs cannot be set
 */
class GitPullRequestCacheNotAvailableException: BaseException("Cache for Git pull requests is not available")