package net.nemerosa.ontrack.extension.notifications.channels

/**
 * Getting access to the list of notification channels.
 */
interface NotificationChannelRegistry {

    /**
     * Gets the list of available channels
     */
    val channels: List<NotificationChannel<*>>

    fun findChannel(type: String): NotificationChannel<*>?

}