package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode

/**
 * Channel for a subscription.
 *
 * @property channel Type of channel to send the event to
 * @property channelConfig Specific configuration of the channel
 */
data class EventSubscriptionChannel(
    val channel: String,
    val channelConfig: JsonNode,
)