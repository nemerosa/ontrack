package net.nemerosa.ontrack.git

import net.nemerosa.ontrack.extension.api.CacheConfigExtension
import net.nemerosa.ontrack.git.support.GitRepositoryClientFactoryImpl.Companion.CACHE_GIT_REPOSITORY_CLIENT
import org.springframework.stereotype.Component

@Component
class GitRepositoryCacheConfigExtension : CacheConfigExtension {
    override val caches: Map<String, String> = mapOf(
            CACHE_GIT_REPOSITORY_CLIENT to "maximumSize=10,expireAfterWrite=1d,recordStats"
    )
}