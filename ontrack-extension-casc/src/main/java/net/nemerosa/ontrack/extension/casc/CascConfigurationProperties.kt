package net.nemerosa.ontrack.extension.casc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Configuration properties for the configuration as code
 */
@Component
@ConfigurationProperties(prefix = "ontrack.config.casc")
class CascConfigurationProperties {

    /**
     * Is the configuration as code enabled?
     */
    var enabled = true

    /**
     * List of resources to load and to monitor for changes
     */
    var locations: List<String> = emptyList()

}