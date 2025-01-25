package net.nemerosa.ontrack.extension.notifications.channels

import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionConfigException

fun ValidatedNotificationChannelConfig<*>.throwException(): Nothing {
    val message = if (exception != null) {
        if (message.isNullOrBlank()) {
            exception.message ?: exception::class.java.simpleName
        } else {
            val exceptionMessage = exception.message ?: exception::class.java.simpleName
            if (exceptionMessage.trim() != message.trim()) {
                "$message: $exceptionMessage"
            } else {
                message
            }
        }
    } else if (!message.isNullOrBlank()) {
        message
    } else if (config == null) {
        "Could not parse the notification configuration"
    } else {
        "Unexpected notification configuration error"
    }
    throw EventSubscriptionConfigException(message)
}