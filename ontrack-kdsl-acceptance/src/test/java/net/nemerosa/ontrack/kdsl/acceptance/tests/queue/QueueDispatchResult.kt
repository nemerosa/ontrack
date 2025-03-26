package net.nemerosa.ontrack.kdsl.acceptance.tests.queue

/**
 * Result of the dispatching of a message to a queue.
 *
 * @property type Result of the dispatching
 * @property id Queue message ID is applicable
 * @property message Additional information
 * @property routingKey Routing key which was used
 */
data class QueueDispatchResult(
        val type: QueueDispatchResultType,
        val id: String?,
        val message: String? = null,
        val routingKey: String? = null,
)