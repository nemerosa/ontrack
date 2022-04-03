package net.nemerosa.ontrack.extension.notifications.dispatching

enum class NotificationDispatchingResult {
    SENT,
    SUBSCRIPTION_DISABLED,
    CHANNEL_UNKNOWN,
    CHANNEL_DISABLED,
    UNSENT
}
