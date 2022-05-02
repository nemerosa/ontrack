package net.nemerosa.ontrack.extension.notifications.channels

class ValidatedNotificationChannelConfig<C>
private constructor(
    val config: C?,
    val message: String?,
) {

    fun isOk() = config != null

    companion object {
        fun <C> config(value: C) = ValidatedNotificationChannelConfig(value, null)
        fun <C> message(value: String) = ValidatedNotificationChannelConfig<C>(null, value)
    }
}