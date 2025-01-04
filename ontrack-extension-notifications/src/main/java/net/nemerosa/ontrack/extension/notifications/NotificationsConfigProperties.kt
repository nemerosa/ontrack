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
    var enabled: Boolean = false

    /**
     * In-memory channel?
     */
    var inMemory = InMemoryChannelProperties()

    /**
     * Dispatching configuration
     */
    var dispatching: DispatchingProperties = DispatchingProperties()

    /**
     * Processing configuration
     */
    var processing: ProcessingProperties = ProcessingProperties()

    /**
     * Webhook channel configuration
     */
    var webhook = WebhookChannelProperties()

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
     * Webhook channel configuration
     */
    class WebhookChannelProperties {
        /**
         * Internal webhoook
         */
        var internal = WebhookInternalEndpointProperties()
    }

    /**
     * Webhook internal channel configuration.
     *
     * To be used for test only.
     */
    class WebhookInternalEndpointProperties {
        /**
         * Enabled?
         */
        @APIDescription("Are internal webhooks enabled?")
        var enabled = false
    }

    /**
     * In-memory channel configuration
     */
    class InMemoryChannelProperties {
        /**
         * Enabled?
         */
        @APIDescription("Is the in-memory notification channel enabled? Used for testing only.")
        var enabled = false
    }

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
        @APIDescription("Is asynchronous processing of notifications enabled?")
        var async: Boolean = true
    }

    /**
     * Configuration for dispatching queuing
     */
    class DispatchingQueuingProperties : AbstractQueuingProperties()

    /**
     * Configuration for processing queuing
     */
    class ProcessingQueuingProperties(
        /**
         * Maximum parallel processing of queues
         */
        @APIDescription("Maximum parallel processing of queues")
        var concurrency: Int = 10
    ) : AbstractQueuingProperties()

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.config.extension.notifications"
    }

}