package net.nemerosa.ontrack.extension.notifications.channels

class ValidatedNotificationChannelConfig<C>
private constructor(
    val config: C?,
    val message: String?,
    val exception: Exception?,
) {

    fun isOk() = config != null && exception == null

    companion object {
        fun <C> config(value: C) = ValidatedNotificationChannelConfig(value, null, null)
        fun <C> error(exception: Exception) =
            ValidatedNotificationChannelConfig<C>(null, exception.message ?: "Cannot parse channel configuration", exception)
    }
}