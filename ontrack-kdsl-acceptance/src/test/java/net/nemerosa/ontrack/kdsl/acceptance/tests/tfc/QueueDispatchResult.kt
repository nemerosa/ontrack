package net.nemerosa.ontrack.kdsl.acceptance.tests.tfc

/**
 * Result of the dispatching of a message to a queue.
 *
 * @property type Result of the dispatching
 * @property id Queue message ID is applicable
 * @property message Additional information
 */
data class QueueDispatchResult(
    val type: String,
    val id: String?,
    val message: String? = null,
)