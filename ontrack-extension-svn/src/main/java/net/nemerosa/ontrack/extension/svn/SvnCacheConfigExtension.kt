package net.nemerosa.ontrack.extension.svn

import net.nemerosa.ontrack.extension.api.CacheConfigExtension
import org.springframework.stereotype.Component

@Component
class SvnCacheConfigExtension: CacheConfigExtension {
    override val caches: Map<String, String> = mapOf(
            "svnChangeLog" to "maximumSize=20,expireAfterWrite=10m,recordStats"
    )
}