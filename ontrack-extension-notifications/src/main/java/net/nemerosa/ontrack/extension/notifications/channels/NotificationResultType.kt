package net.nemerosa.ontrack.extension.notifications.channels

enum class NotificationResultType(
    val running: Boolean,
) {

    OK(running = false),

    ONGOING(running = true),

    NOT_CONFIGURED(running = false),

    INVALID_CONFIGURATION(running = false),

    DISABLED(running = false),

    ERROR(running = false),

    @Deprecated("Timeout was introduced in 4.10.3 but is no longer used. No replacement not deletion is planned at the moment.")
    TIMEOUT(running = false),

    ASYNC(running = true),

}