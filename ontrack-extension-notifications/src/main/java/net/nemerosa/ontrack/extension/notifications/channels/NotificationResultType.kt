package net.nemerosa.ontrack.extension.notifications.channels

enum class NotificationResultType {

    OK,

    ONGOING,

    NOT_CONFIGURED,

    INVALID_CONFIGURATION,

    DISABLED,

    ERROR,

    @Deprecated("Timeout was introduced in 4.10.3 but is no longer used. No replacement not deletion is planned at the moment.")
    TIMEOUT,

    ASYNC,

}