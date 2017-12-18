package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.CacheConfigExtension
import org.springframework.stereotype.Component

/**
 * List of core caches.
 */
@Component
class CoreCacheConfigExtension : CacheConfigExtension {
    override val caches: Map<String, String>
        get() = mapOf(
                "properties" to "maximumSize=1000,expireAfterWrite=1d,recordStats"
        )
}
