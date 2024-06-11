package net.nemerosa.ontrack.extension.notifications.subscriptions

import com.fasterxml.jackson.databind.JsonNode

/**
 * Subscription record, can be used for storage
 */
data class SubscriptionRecord(
    val name: String,
    val channel: String,
    val channelConfig: JsonNode,
    val events: Set<String>,
    val keywords: String?,
    val disabled: Boolean,
    val origin: String,
    val contentTemplate: String?,
)
