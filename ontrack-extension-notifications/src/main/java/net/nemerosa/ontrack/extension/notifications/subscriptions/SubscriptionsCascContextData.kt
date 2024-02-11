package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

data class SubscriptionsCascContextData(
    @APIDescription("List of events to listen to")
    val events: List<String>,
    @APIDescription("Keywords to filter the events")
    val keywords: String?,
    @APIDescription("Channel to send notifications to")
    val channel: String,
    @APIDescription("Configuration of the channel")
    @get:JsonProperty("channel-config")
    val channelConfig: JsonNode,
    @APIDescription("Is this channel disabled?")
    val disabled: Boolean? = null,
    @APIDescription("Optional template to use for the message")
    val contentTemplate: String?,
) {
    fun normalized() = SubscriptionsCascContextData(
        events = events.sorted(),
        keywords = keywords,
        channel = channel,
        channelConfig = channelConfig,
        disabled = disabled ?: false,
        contentTemplate = contentTemplate,
    )
}