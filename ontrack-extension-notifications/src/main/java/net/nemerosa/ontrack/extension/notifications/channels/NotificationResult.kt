package net.nemerosa.ontrack.extension.notifications.channels

data class NotificationResult(
    val type: NotificationResultType,
    val message: String?,
) {
    companion object {
        fun ok() = NotificationResult(NotificationResultType.OK, null)
        fun notConfigured(message: String) = NotificationResult(NotificationResultType.NOT_CONFIGURED, message)
        fun invalidConfiguration() = NotificationResult(NotificationResultType.INVALID_CONFIGURATION, "Invalid configuration")
        fun error(message: String) = NotificationResult(NotificationResultType.ERROR, message)
        fun disabled(message: String) = NotificationResult(NotificationResultType.DISABLED, message)
    }
}