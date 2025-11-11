package net.nemerosa.ontrack.extension.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = NotificationsConfigProperties.PREFIX)
@APIName("Notifications configuration")
@APIDescription("General configuration for the notifications.")
class NotificationsConfigProperties {

    @APIDescription("Are the notifications enabled?")
    var enabled: Boolean = true

    /**
     * Processing configuration
     */
    var processing: ProcessingProperties = ProcessingProperties()

    /**
     * Mail configuration
     */
    var mail = MailProperties()

    /**
     * Mail configuration properties
     */
    class MailProperties {
        @APIDescription("From address for the email notifications")
        var from = "no-reply@localhost"
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
    class ProcessingQueuingProperties {
        /**
         * Default number of queues for the event listener
         */
        @APIDescription("Default number of queues for the event listener")
        var listenerQueues: Int = 5
        /**
         * Default number of queues for the processing
         */
        @APIDescription("Default number of queues for the processing")
        var processingQueues: Int = 5
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.config.extension.notifications"
    }

}