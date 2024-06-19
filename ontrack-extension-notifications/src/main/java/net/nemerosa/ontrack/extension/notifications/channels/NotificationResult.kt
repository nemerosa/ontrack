package net.nemerosa.ontrack.extension.notifications.channels

import net.nemerosa.ontrack.model.annotations.APIDescription

data class NotificationResult<R>(
    @APIDescription("Type of result")
    val type: NotificationResultType,
    @APIDescription("Result message")
    val message: String?,
    @APIDescription("Output of the channel")
    val output: R?,
) {
    companion object {
        fun <R> ok(output: R) = NotificationResult(NotificationResultType.OK, null, output = output)
        fun <R> ongoing(output: R) = NotificationResult(NotificationResultType.ONGOING, null, output = output)
        fun <R> notConfigured(message: String) =
            NotificationResult<R>(NotificationResultType.NOT_CONFIGURED, message, output = null)

        fun <R> invalidConfiguration(message: String? = null) =
            NotificationResult<R>(
                NotificationResultType.INVALID_CONFIGURATION,
                message ?: "Invalid configuration",
                output = null
            )

        fun <R> error(message: String, output: R? = null) =
            NotificationResult<R>(NotificationResultType.ERROR, message, output = output)

        fun <R> disabled(message: String) = NotificationResult<R>(NotificationResultType.DISABLED, message, null)
    }
}