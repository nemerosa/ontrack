package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Static configuration for the auto versioning.
 */
@Component
@ConfigurationProperties(prefix = AutoVersioningConfigProperties.PREFIX)
@APIName("Auto-versioning configuration")
@APIDescription("Configuration of the auto-versioning")
class AutoVersioningConfigProperties {

    var queue = QueueConfigProperties()
    var scheduling = SchedulingConfigProperties()

    class QueueConfigProperties {
        @APIDescription("Cancelling the previous orders for the same source and same target if a new order comes in")
        var cancelling: Boolean = true

        @APIDescription("Default number of RabbitMQ queues to use")
        var scale: Int = 10
    }

    class SchedulingConfigProperties {
        @APIDescription("Scheduling enabled?")
        var enabled: Boolean = true
        @APIDescription("Cron expression for the scheduling of the auto-versioning orders")
        var cron: String = DEFAULT_CRON
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.auto-versioning"
        /**
         * Default cron for the scheduling
         */
        const val DEFAULT_CRON = "0 */30 * * * *" // Every 30 minutes
    }

}