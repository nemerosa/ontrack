package net.nemerosa.ontrack.extension.notifications.channels

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event

/**
 * Makes the link between an event to be sent and an actual notification backend.
 *
 * @param C Type of the configuration for this channel.
 */
interface NotificationChannel<C> {

    fun validate(channelConfig: JsonNode): ValidatedNotificationChannelConfig<C>

    fun publish(config: C, event: Event): NotificationResult

    /**
     * Type of the channel, used as an identifier for the serialization of the subscriptions.
     */
    val type: String

    /**
     * Is this channel enabled?
     */
    val enabled: Boolean

}