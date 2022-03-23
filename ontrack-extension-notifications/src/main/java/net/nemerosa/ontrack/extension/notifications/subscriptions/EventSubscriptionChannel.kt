package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Channel for a subscription.
 *
 * @property channel Type of channel to send the event to
 * @property channelConfig Specific configuration of the channel
 */
@APIDescription("Channel for a subscription.")
data class EventSubscriptionChannel(
    @APIDescription("Type of channel to send the event to")
    val channel: String,
    @APIDescription("Specific configuration of the channel")
    val channelConfig: JsonNode,
)