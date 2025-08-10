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

    class QueueConfigProperties {
        @APIDescription(
            """
                By default, Ontrack uses RabbitMQ queue to manage the auto versioning processes.
                Disabling this mechanism is not recommended and is used only for internal testing.
            """
        )
        @Deprecated("Will be removed in V5. Not used any longer.")
        var async: Boolean = true

        @APIDescription("Cancelling the previous orders for the same source and same target if a new order comes in")
        var cancelling: Boolean = true

        @APIDescription("Default number of RabbitMQ queues to use")
        var scale: Int = 10

        @APIDescription("List of projects which must have dedicated queues")
        @Deprecated("Will be removed in V5. Not used any longer: queues are dispatched and load-balanced automatically.")
        var projects: List<String> = emptyList()
    }

    companion object {
        /**
         * Prefix for the properties
         */
        const val PREFIX = "ontrack.extension.auto-versioning"
    }

}