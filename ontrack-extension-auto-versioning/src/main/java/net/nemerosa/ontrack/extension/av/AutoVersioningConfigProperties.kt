package net.nemerosa.ontrack.extension.av

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Static configuration for the auto versioning.
 */
@Component
@ConfigurationProperties(prefix = AutoVersioningConfigProperties.PREFIX)
class AutoVersioningConfigProperties {

    var queue = QueueConfigProperties()

    class QueueConfigProperties {
        var async: Boolean = true
        var scale: Int = 1
        var projects: List<String> = emptyList()
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.auto-versioning"
    }

}