package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Event subscription record")
data class EventSubscriptionPayload(
    @APIDescription("Unique ID for this subscription")
    val id: String,
    @APIDescription("Channel to send this event to")
    val channel: String,
    @APIDescription("Channel configuration")
    val channelConfig: JsonNode,
    @APIDescription("List of events types to subscribe to")
    val events: List<String>,
    @APIDescription("Optional space-separated list of tokens to look for in the events")
    val keywords: String?,
    @APIDescription("If this subscription is disabled")
    val disabled: Boolean,
)