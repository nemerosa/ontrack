package net.nemerosa.ontrack.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ontrack.config.cache")
class CacheConfigProperties {
    /**
     * Caffeine specifications
     */
    var specs = mutableMapOf<String, String>()
}
