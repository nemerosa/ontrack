package net.nemerosa.ontrack.extension.notifications.channels

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * @param id ID returned by the channel
 */
data class NotificationResult(
    @APIDescription("Type of result")
    val type: NotificationResultType,
    @APIDescription("Optional ID for the message into the channel")
    val id: String?,
    @APIDescription("Result message")
    val message: String?,
) {
    companion object {
        fun ok(id: String? = null) = NotificationResult(NotificationResultType.OK, id, null)
        fun notConfigured(message: String) = NotificationResult(NotificationResultType.NOT_CONFIGURED, null, message)
        fun invalidConfiguration() = NotificationResult(NotificationResultType.INVALID_CONFIGURATION, null, "Invalid configuration")
        fun error(message: String, id: String? = null) = NotificationResult(NotificationResultType.ERROR, id, message)
        fun disabled(message: String) = NotificationResult(NotificationResultType.DISABLED, null, message)
    }
}