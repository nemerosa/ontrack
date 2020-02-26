package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.api.CacheConfigExtension
import org.springframework.stereotype.Component

/**
 * Configuration of caching for the Git module
 */
@Component
class GitCacheConfigExtension: CacheConfigExtension {
    override val caches: Map<String, String> = mapOf(
            CACHE_GIT_CHANGE_LOG to "maximumSize=20,expireAfterWrite=10m,recordStats"
    )
}