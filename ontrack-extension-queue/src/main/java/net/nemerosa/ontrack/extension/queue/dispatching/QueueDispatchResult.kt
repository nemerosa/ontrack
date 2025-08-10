package net.nemerosa.ontrack.extension.queue.dispatching

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Result of the dispatching of a message to a queue.
 *
 * @property type Result of the dispatching
 * @property id Queue message ID is applicable
 * @property message Additional information
 * @property routingKey Routing key used by the message
 */
data class QueueDispatchResult(
        @APIDescription("Result of the dispatching")
        val type: QueueDispatchResultType,
        @APIDescription("Queue message ID is applicable")
        val id: String?,
        @APIDescription("Additional information")
        val message: String? = null,
        @APIDescription("Routing key used by the message")
        val routingKey: String? = null,
)