package net.nemerosa.ontrack.extension.notifications

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = NotificationsConfigProperties.PREFIX)
class NotificationsConfigProperties {
    /**
     * Are the notifications enabled?
     */
    var enabled: Boolean = true

    /**
     * Dispatching configuration
     */
    var dispatching: DispatchingProperties = DispatchingProperties()

    /**
     * Processing configuration
     */
    var processing: ProcessingProperties = ProcessingProperties()

    /**
     * Dispatching configuration
     */
    class DispatchingProperties {
        /**
         * Queuing configuration
         */
        var queue: DispatchingQueuingProperties = DispatchingQueuingProperties()
    }

    /**
     * Processing configuration
     */
    class ProcessingProperties {
        /**
         * Queuing configuration
         */
        var queue: ProcessingQueuingProperties = ProcessingQueuingProperties()
    }

    /**
     * Configuration for queuing
     */
    abstract class AbstractQueuingProperties {
        /**
         * Async processing enabled?
         */
        var async: Boolean = true
    }

    /**
     * Configuration for dispatching queuing
     */
    class DispatchingQueuingProperties : AbstractQueuingProperties()

    /**
     * Configuration for processing queuing
     */
    class ProcessingQueuingProperties : AbstractQueuingProperties()

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.config.extension.notifications"
    }

}