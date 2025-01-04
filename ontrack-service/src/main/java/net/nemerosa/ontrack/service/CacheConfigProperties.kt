package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ontrack.config.cache")
@DocumentationIgnore
class CacheConfigProperties {
    /**
     * Caffeine specifications
     */
    var specs = mutableMapOf<String, String>()
}
