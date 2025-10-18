package net.nemerosa.ontrack.extension.notifications.ci

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.model.annotations.APIDescription

data class NotificationsCIConfigItem(
    @APIDescription("Unique name of the subscription in its scope.")
    val name: String,
    @APIDescription("Targeting a promotion instead of the branch. The promotion must have been configured first.")
    val promotion: String? = null,
    @APIDescription("List of events types to subscribe to")
    val events: List<String> = emptyList(),
    @APIDescription("Optional space-separated list of tokens to look for in the events")
    val keywords: String? = null,
    @APIDescription("Type of channel to send the event to")
    val channel: String? = null,
    val channelConfig: JsonNode = NullNode.instance,
    @APIDescription("Optional template to use for the message")
    val contentTemplate: String? = null,
)