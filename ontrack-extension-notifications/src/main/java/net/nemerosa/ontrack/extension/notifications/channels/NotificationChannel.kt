package net.nemerosa.ontrack.extension.notifications.channels

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form

/**
 * Makes the link between an event to be sent and an actual notification backend.
 *
 * @param C Type of the configuration for this channel.
 * @param R Type of output returned by the channel
 */
interface NotificationChannel<C, R> {

    fun validate(channelConfig: JsonNode): ValidatedNotificationChannelConfig<C>

    /**
     * Sends an event onto this channel.
     *
     * @param config Configuration for the channel
     * @param event Event to send notification about
     * @param template Alternative template to the default event message
     * @return Response by the channel
     */
    fun publish(config: C, event: Event, template: String?): NotificationResult<R>

    /**
     * Given a search token, returns a configuration which would match a JSON.
     */
    fun toSearchCriteria(text: String): JsonNode

    /**
     * Given a configuration, returns a display text for it
     */
    @Deprecated("Will be removed in V5. Only Next UI is used.")
    fun toText(config: C): String

    /**
     * Gets the form for the channel's configuration
     */
    @Deprecated("Will be removed in V5. Only Next UI is used.")
    fun getForm(c: C?): Form

    /**
     * Type of the channel, used as an identifier for the serialization of the subscriptions.
     */
    val type: String

    /**
     * Is this channel enabled?
     */
    val enabled: Boolean

}