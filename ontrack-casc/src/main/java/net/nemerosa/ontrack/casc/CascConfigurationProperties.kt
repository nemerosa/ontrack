package net.nemerosa.ontrack.casc

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * Configuration properties for the configuration as code
 */
@Component
@ConfigurationProperties(prefix = "ontrack.config.casc")
@Validated
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